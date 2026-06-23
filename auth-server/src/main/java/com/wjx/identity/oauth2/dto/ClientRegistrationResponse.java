package com.wjx.identity.oauth2.dto;

public record ClientRegistrationResponse(
        String client_id,
        String redirect_uri,
        String scope,
        String certificate_subject
) {
}
