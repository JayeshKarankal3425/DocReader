package com.spring.DocReader.dto;

import com.spring.DocReader.entity.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentMetaDataDto {


    private UUID id;
    private  String filename;
    private  String contentType;
    private  Long fileSize;
    private Integer totalPages;
    private  Integer totalChunks;
    private DocumentStatus status;
    private  String errorMessage;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
}
