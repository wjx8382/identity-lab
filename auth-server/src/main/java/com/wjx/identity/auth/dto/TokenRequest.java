package com.wjx.identity.auth.dto;

public record TokenRequest(
        String grant_type,
        String code,
        String client_id,
        String client_secret
) {
}
