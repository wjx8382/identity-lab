package com.wjx.identity.oauth2.dto;

public record OAuth2TokenResponse(
        String access_token,
        String refresh_token,
        String token_type,
        Long expires_in
) {
}
