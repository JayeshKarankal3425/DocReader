package com.spring.DocReader.dto;


import com.spring.DocReader.entity.Role;

public record UserDto(
        Long id,
        String username,
        String email,
        Role role
) {
}
