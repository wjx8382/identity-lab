package com.wjx.identity.oauth2.dto;

public record ParRequest(
        String client_id,
        String redirect_uri,
        String scope,
        String state,
        String code_challenge,
        String code_challenge_method
) {
}
