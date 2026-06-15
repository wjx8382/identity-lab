package com.wjx.identity.oauth2.dto;

public record IntrospectResponse(
        boolean active,
        String sub,
        String scope,
        Long exp
) {
}
