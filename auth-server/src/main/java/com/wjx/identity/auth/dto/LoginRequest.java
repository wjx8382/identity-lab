package com.wjx.identity.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
