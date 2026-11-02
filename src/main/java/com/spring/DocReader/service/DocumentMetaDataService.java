package com.spring.DocReader.service;

import com.spring.DocReader.dto.DocumentMetaDataDto;
import com.spring.DocReader.dto.DocumentResponseDto;
import com.spring.DocReader.entity.DocumentMetadata;
import com.spring.DocReader.entity.DocumentStatus;
import com.spring.DocReader.entity.User;
import com.spring.DocReader.exception.DocumentProcessingException;
import com.spring.DocReader.exception.ResourceNotFoundException;
import com.spring.DocReader.repository.DocumentMetaDataRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentMetaDataService {

    private static final Logger log = (Logger) LoggerFactory.getLogger(DocumentMetaDataService.class);
    private final DocumentMetaDataRepo repo;
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper modelMapper;
    private final DocumentParserService parserService;
    private final DocumentIngestionService ingestionService;

    public DocumentResponseDto uploadAndProcessDocument(MultipartFile file, User user) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octat-stream";

        // Storing user details with document
        DocumentMetadata documentMetadata = DocumentMetadata
                .builder()
                .fileName(fileName)
                .contentType(contentType)
                .status(DocumentStatus.PROCESSING)
                .fileSize(file.getSize())
                .user(user)
                .build();

        documentMetadata.setCreatedAt(LocalDateTime.now());
        documentMetadata = repo.save(documentMetadata);

        List<Document> parseDocs = null;
        Integer chunksCreated = 0;
        try {
            parseDocs = parserService.parse(file);

            chunksCreated = ingestionService.ingest(documentMetadata, parseDocs);
        }
        catch(DocumentProcessingException e){
            log.info("Document metadata deleting due to failed processing");
            repo.delete(documentMetadata);
            throw e;
        }
        return DocumentResponseDto.builder()
                .id(documentMetadata.getId())
                .fileName(documentMetadata.getFileName())
                .fileSize(documentMetadata.getFileSize())
                .chunksCreated(chunksCreated)
                .status(documentMetadata.getStatus())
                .message("Document successfully processed and indexed")
                .userId(user.getId())
                .build();
    }

    public List<DocumentResponseDto> uploadMultipleDocuments(List<MultipartFile> files,User user) {


        List<DocumentResponseDto> responseDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            DocumentResponseDto result = this.uploadAndProcessDocument(file,user);
            responseDtos.add(result);
        }

        return responseDtos;

    }

    //    to get all the documents: This method is useful for admin
    public List<DocumentMetaDataDto> getAllDocuments() {

        List<DocumentMetadata> allDocuments = repo.findAllByOrderByCreatedAtDesc();
        return allDocuments.stream()
                .map(documentMetaData-> modelMapper.map(documentMetaData,DocumentMetaDataDto.class))
                .toList();

    }

    public DocumentMetaDataDto getDocumentById(UUID id) {
        DocumentMetadata documentMetadata = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document with given id not found !!"));
        return modelMapper.map(documentMetadata, DocumentMetaDataDto.class);


    }
    public DocumentMetaDataDto getDocumentByIdAndUser(UUID id,User user) {
        DocumentMetadata documentMetadata = repo.findByIdAndUser(id,user).orElseThrow(() -> new ResourceNotFoundException("Document with given id not found !!"));
        return modelMapper.map(documentMetadata, DocumentMetaDataDto.class);


    }

    public List<DocumentMetaDataDto> getAllDocumentByUser(User user){
        List<DocumentMetadata> allDocuments =  repo.findByUser(user);
        return allDocuments.stream()
                .map(documentMetaData-> modelMapper.map(documentMetaData,DocumentMetaDataDto.class))
                .toList();
    }

    @Transactional
    public void deleteDocument(UUID id) {
        DocumentMetadata documentMetadata = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document with given id not found !!"));


        repo.delete(documentMetadata);
        //delete the vector entries
        try {
            String deleteVectorsSql = "DELETE FROM vector_store WHERE metadata->>'documentId' = ?";
            int deletedCount = jdbcTemplate.update(deleteVectorsSql, id.toString());
            log.info("Deleted {} vector chunks for document id {} ", deletedCount, id);

        } catch (Exception e) {
            log.warn("cloud not delete vectors from vector store directly: {}", e.getMessage());
        }

    }

}
