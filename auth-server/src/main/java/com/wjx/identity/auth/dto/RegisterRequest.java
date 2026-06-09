package com.wjx.identity.auth.dto;

public record RegisterRequest(
        String username,
        String password,
        String email
) {
}
