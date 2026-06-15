package com.wjx.identity.oauth2.controller;

import com.wjx.identity.security.config.KeyPairProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@RequiredArgsConstructor
public class JwksController {

    private final KeyPairProvider keyPairProvider;

    @GetMapping("/jwks.json")
    public Map<String, Object> jwks() {

        RSAPublicKey publicKey =
                (RSAPublicKey) keyPairProvider.getPublicKey();

        String modules =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                publicKey
                                        .getModulus()
                                        .toByteArray()
                        );

        String exponent =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                publicKey
                                        .getPublicExponent()
                                        .toByteArray()
                        );

        return Map.of(
                "keys",
                List.of(
                        Map.of(
                                "kty", "RSA",
                                "alg", "RS256",
                                "use", "sig",
                                "kid", "identity-lab-key",
                                "n", modules,
                                "e", exponent
                        )
                )
        );
    }
}
