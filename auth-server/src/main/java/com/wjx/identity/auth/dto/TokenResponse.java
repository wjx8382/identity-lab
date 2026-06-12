package com.wjx.identity.auth.dto;

public record TokenResponse(
        String access_token,
        String code_verifier
) {
}
