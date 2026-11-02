package com.spring.DocReader.controller;


import com.spring.DocReader.dto.ApiResponse;
import com.spring.DocReader.dto.DocumentMetaDataDto;
import com.spring.DocReader.dto.DocumentResponseDto;
import com.spring.DocReader.entity.User;
import com.spring.DocReader.service.DocumentMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(
        name="Document Management",
        description = "Endpoint for uploading, listing, and managing documents and their vector embeddings"
)
@RequiredArgsConstructor
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentMetaDataService documentService;




    @PostMapping(value="/upload", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload and index document(PDF, DOCX, TEXT, CSV, MD",
            description = "This api is used to upload and index documents files"
    )
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(@RequestParam("file")  MultipartFile file
                    ,Authentication authentication
            ){

        log.info("document uploading started.....");
        User user = (User) authentication.getPrincipal();
        DocumentResponseDto documentResponseDto = documentService.uploadAndProcessDocument(file,user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DocumentResponseDto>builder()
                .success(true)
                .data(documentResponseDto)
                .timeStamp(LocalDateTime.now())
                .message("Document uploaded and Indexed successfully")
                .build());
    }



    //    api to upload multiple documents
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload and index multiple documents simultaneously",
            description = "This api is used to upload and index multiple documents."
    )
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> uploadMultiple(
            @RequestParam("files")
            List<MultipartFile> files,
            Authentication authentication
    ) {

        User user=(User)authentication.getPrincipal();
        List<DocumentResponseDto> responseDtos = documentService.uploadMultipleDocuments(files,user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<List<DocumentResponseDto>>
                                        builder()
                                .message("Document uploaded successfully")
                                .success(true)
                                .timeStamp(LocalDateTime.now())
                                .data(responseDtos)
                                .build()
                );
    }


    @GetMapping
    @Operation(summary = "List all uploaded documents and their indexing status")
    public ResponseEntity<ApiResponse<List<DocumentMetaDataDto>>> getAllDocuments(){
        java.util.List<DocumentMetaDataDto> documents =documentService.getAllDocuments();
        return ResponseEntity.ok(
                ApiResponse.<List<DocumentMetaDataDto>>
                                builder()
                        .message("All documents is here")
                        .success(true)
                        .timeStamp(LocalDateTime.now())
                        .data(documents)
                        .build()
        );
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get metadata of a specific document by ID")
    public ResponseEntity<ApiResponse<DocumentMetaDataDto>> getDocumentById(@PathVariable UUID id) {
        DocumentMetaDataDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(
                ApiResponse.<DocumentMetaDataDto>
                                builder()
                        .message("Single document is here")
                        .success(true)
                        .timeStamp(LocalDateTime.now())
                        .data(document)
                        .build()
        );
    }


    @GetMapping("/user")
    @Operation(summary = "List all uploaded documents and their indexing status")
    public ResponseEntity<ApiResponse<List<DocumentMetaDataDto>>> getAllDocumentsOfLoggedInUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        java.util.List<DocumentMetaDataDto> documents =documentService.getAllDocumentByUser(user);
        return ResponseEntity.ok(
                ApiResponse.<List<DocumentMetaDataDto>>
                                builder()
                        .message("All documents are here")
                        .success(true)
                        .timeStamp(LocalDateTime.now())
                        .data(documents)
                        .build()
        );
    }


    @GetMapping("/user/{id}")
    @Operation(summary = "Get metadata of a specific document by ID")
    public ResponseEntity<ApiResponse<DocumentMetaDataDto>> getDocumentByIdAndUserOfLoggedInUser(@PathVariable UUID id,Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        DocumentMetaDataDto document = documentService.getDocumentByIdAndUser(id,user);
        return ResponseEntity.ok(
                ApiResponse.<DocumentMetaDataDto>
                                builder()
                        .message("Single document is here")
                        .success(true)
                        .timeStamp(LocalDateTime.now())
                        .data(document)
                        .build()
        );
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a document and purge its vector embeddings from vector store")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>
                                builder()
                        .message("Document deleted successfully")
                        .success(true)
                        .timeStamp(LocalDateTime.now())
                        .data(null)
                        .build()
        );
    }


}
