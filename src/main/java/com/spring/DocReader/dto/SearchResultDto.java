package com.spring.DocReader.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDto {
    private String query;
    private Integer totalMatches;
    private List<CitationDto> matches;
}
