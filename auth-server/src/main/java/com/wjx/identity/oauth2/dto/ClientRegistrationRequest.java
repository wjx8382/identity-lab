package com.wjx.identity.oauth2.dto;

public record ClientRegistrationRequest(
        String client_id,
        String client_secret,
        String redirect_uri,
        String scope,
        String certificate_subject
) {
}
