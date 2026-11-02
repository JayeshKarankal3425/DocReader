
package com.spring.DocReader.service;

import com.spring.DocReader.config.AppProperties;
import com.spring.DocReader.entity.DocumentMetadata;
import com.spring.DocReader.entity.DocumentStatus;
import com.spring.DocReader.entity.User;
import com.spring.DocReader.exception.DocumentProcessingException;
import com.spring.DocReader.repository.DocumentMetaDataRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;
    private final DocumentMetaDataRepo documentMetaDataRepo;
    private final AppProperties appProperties;

    public Integer ingest(
            DocumentMetadata metadata,
            List<Document> parseDocs) {

        log.info(
                "Ingesting document [id={}, name={}, parsedPages={}]",
                metadata.getId(),
                metadata.getFileName(),
                parseDocs.size()
        );

        try {
            metadata.setStatus(DocumentStatus.PROCESSING);
            metadata.setTotalPages(parseDocs.size());
            documentMetaDataRepo.save(metadata);

            // 1. Split parsed documents into chunks.
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(
                            appProperties.getRag().getChunkSize())
                    .withMinChunkSizeChars(
                            appProperties.getRag().getMinChunkSizeChars())
                    .withMinChunkLengthToEmbed(
                            appProperties.getRag().getMinChunkLengthToEmbed())
                    .withMaxNumChunks(
                            appProperties.getRag().getMaxNumChunks())
                    .withKeepSeparator(true)
                    .build();

            List<Document> chunks = splitter.apply(parseDocs);

            log.info(
                    "Generated {} chunks for {}",
                    chunks.size(),
                    metadata.getFileName()
            );

            if (chunks.isEmpty()) {
                throw new DocumentProcessingException(
                        "No text chunks generated. Document may be empty or unscannable."
                );
            }

            // 2. Clean text and enrich metadata.
            List<Document> enrichedChunks = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                Document chunk = chunks.get(i);

                String text = chunk.getText()
                        .replace("\u0000", "")
                        .trim();

                if (text.isEmpty()) {
                    log.warn("Skipping empty chunk at index {}", i);
                    continue;
                }

                Map<String, Object> enrichedMetadata =
                        new HashMap<>(chunk.getMetadata());

                enrichedMetadata.put(
                        "documentId",
                        metadata.getId().toString()
                );
                enrichedMetadata.put("fileName", metadata.getFileName());
                enrichedMetadata.put("contentType", metadata.getContentType());
                enrichedMetadata.put("chunkIndex", i);
                // imp step for user isolation
                enrichedMetadata.put("userId",metadata.getUser().getId());

                Object pageNumber =
                        chunk.getMetadata().get("page_number");

                if (pageNumber == null) {
                    pageNumber = chunk.getMetadata().get("pageNumber");
                }

                if (pageNumber != null) {
                    enrichedMetadata.put("pageNumber", pageNumber);
                }

                enrichedChunks.add(
                        new Document(text, enrichedMetadata)
                );
            }

            if (enrichedChunks.isEmpty()) {
                throw new DocumentProcessingException(
                        "No valid text chunks remain after cleaning."
                );
            }

            // 3. Generate embeddings and save all chunks to PgVector.
            log.info(
                    "Saving {} chunks to PgVectorStore for {}",
                    enrichedChunks.size(),
                    metadata.getFileName()
            );

            vectorStore.add(enrichedChunks);

            // 4. Mark as indexed only after vectorStore.add succeeds.
            metadata.setStatus(DocumentStatus.INDEXED);
            metadata.setTotalChunks(enrichedChunks.size());
            metadata.setErrorMessage(null);
            documentMetaDataRepo.save(metadata);

            log.info(
                    "Successfully indexed document [id={}, name={}, chunks={}]",
                    metadata.getId(),
                    metadata.getFileName(),
                    enrichedChunks.size()
            );

            return enrichedChunks.size();

        } catch (Exception ex) {
            log.error(
                    "Failed to ingest document: {}",
                    metadata.getFileName(),
                    ex
            );

            metadata.setStatus(DocumentStatus.FAILED);
            metadata.setErrorMessage(ex.getMessage());
            documentMetaDataRepo.save(metadata);

            if (ex instanceof DocumentProcessingException) {
                throw (DocumentProcessingException) ex;
            }

            throw new DocumentProcessingException(
                    "Failed to index document: " + ex.getMessage(),
                    ex
            );
        }
    }
}
