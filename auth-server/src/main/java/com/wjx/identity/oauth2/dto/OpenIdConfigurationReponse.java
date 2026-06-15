package com.wjx.identity.oauth2.dto;

import java.util.List;

public record OpenIdConfigurationReponse(
        String issuer,
        String authorization_endpoint,
        String token_endpoint,
        String userinfo_endpoint,
        String jwks_uri,
        List<String> response_types_supported,
        List<String> subject_types_supported,
        List<String> id_token_signing_alg_values_supported
) {
}
