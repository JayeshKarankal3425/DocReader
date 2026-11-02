package com.spring.DocReader.dto;

import com.spring.DocReader.entity.Role;

public record RegisterUserRequest(
        String username,
        String email,
        String password
) {
}
