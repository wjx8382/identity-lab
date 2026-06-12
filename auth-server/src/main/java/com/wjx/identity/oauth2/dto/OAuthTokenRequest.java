package com.wjx.identity.oauth2.dto;

public record OAuthTokenRequest(
        String grant_type,
        String code,
        String client_id,
        String client_secret,
        String code_verifier
) {
}
