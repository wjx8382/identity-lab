package com.wjx.identity.oauth2.dto;

public record UserInfoResponse(
        String sub,
        String name,
        String email
) {
}
