package com.wjx.identity.oauth2.dto;

public record ParResponse(
        String request_uri,
        Long expires_in
) {
}
