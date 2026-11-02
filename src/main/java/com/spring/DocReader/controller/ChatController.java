package com.spring.DocReader.controller;

import com.google.protobuf.Api;
import com.spring.DocReader.dto.*;
import com.spring.DocReader.entity.User;
import com.spring.DocReader.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(
        name = "Chat Management",
        description = "All chat related API's goes here"
)
@RequiredArgsConstructor
public class ChatController {

    public final RagService ragService;

    @PostMapping("/query")
    @Operation(summary = "Ask a question against all documents or specific documents with citations")
    public ResponseEntity<ApiResponse<ChatResponseDto>> askQuestion(
            @Valid @RequestBody ChatRequestDto chatRequestDto
            ){
        ChatResponseDto chatResponseDto = ragService.askQuestion(chatRequestDto);
        return ResponseEntity.ok(
                ApiResponse.<ChatResponseDto>
                        builder()
                        .success(true)
                        .message(null)
                        .data(chatResponseDto)
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/stream")
    @Operation(summary = "Answer using stream event")
    public Flux<String> streamQuestion(
            @Valid @RequestBody ChatRequestDto chatRequestDto
    ){
       return ragService.streamQuestionAnswer(chatRequestDto);
    }


    @PostMapping("/search/similarity")
    @Operation(summary = "Perform semantic similarity search on stored document vectors")
    public ResponseEntity<ApiResponse<SearchResultDto>> searchSimilar(@Valid@RequestBody SearchRequestDto requestDto,Authentication authentication){
        User user = (User) authentication.getPrincipal();
        SearchResultDto searchResultDto = ragService.searchSimilarChunks(requestDto,user);
        return ResponseEntity.ok(
                ApiResponse.<SearchResultDto>
                            builder()
                        .success(true)
                        .message(null)
                        .data(searchResultDto)
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

}