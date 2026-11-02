package com.spring.DocReader.repository;

import com.spring.DocReader.dto.DocumentMetaDataDto;
import com.spring.DocReader.entity.DocumentMetadata;
import com.spring.DocReader.entity.DocumentStatus;
import com.spring.DocReader.entity.User;
import org.springframework.ai.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentMetaDataRepo extends JpaRepository<DocumentMetadata, UUID> {

    List<DocumentMetadata> findByStatus(DocumentStatus status);

    List<DocumentMetadata> findAllByOrderByCreatedAtDesc();

    List<DocumentMetadata> findByUser(User user);

    Optional<DocumentMetadata> findByIdAndUser(UUID uuid, User user);

}