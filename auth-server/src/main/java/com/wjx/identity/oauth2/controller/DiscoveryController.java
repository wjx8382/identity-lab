package com.wjx.identity.oauth2.controller;

import com.wjx.identity.oauth2.dto.OpenIdConfigurationReponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/.well-known")
public class DiscoveryController {

    @GetMapping("openid-configuration")
    public OpenIdConfigurationReponse openIdConfiguration() {
        return new OpenIdConfigurationReponse(
                "http://localhost:8080",
                "http://localhost:8080/oauth2/authorize",
                "http://localhost:8080/oauth2/token",
                "http://localhost:8080/oauth2/userinfo",
                "http://localhost:8080/.well-known/jwks.json",
                List.of("code"),
                List.of("public"),
                List.of("HS256")
        );
    }
}
