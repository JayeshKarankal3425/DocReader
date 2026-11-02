package com.spring.DocReader.dto;

public record LoginResponse(
        String accessToken,
        UserDto user
) {
}
