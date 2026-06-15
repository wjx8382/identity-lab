package com.wjx.identity.oauth2.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record OauthRequest(
//        @RequestParam String client_id,
//        @RequestParam String redirect_uri,
//        @RequestParam String response_type,
//        @RequestParam String scope,
//        @RequestParam(required = false) String state,
//        @RequestParam String code_challenge,
//        @RequestParam String code_challenge_method
        @RequestParam String request_uri
) {
}
