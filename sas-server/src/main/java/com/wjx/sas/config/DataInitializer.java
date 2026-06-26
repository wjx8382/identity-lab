package com.wjx.sas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RegisteredClientRepository registeredClientRepository;

    @Bean
    CommandLineRunner init() {
        return args -> {
            RegisteredClient client =
                    registeredClientRepository.findByClientId(
                            "web-client"
                    );

            if (client != null) {
                return;
            }

            RegisteredClient registeredClient =
                    RegisteredClient.withId(
                                    UUID.randomUUID().toString()
                            )
                            .clientId("web-client")
                            .clientSecret("{noop}web-secret")
                            .clientAuthenticationMethod(
                                    ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                            )
                            .clientSettings(
                                    ClientSettings
                                            .builder()
                                            .requireAuthorizationConsent(true)
                                            .build()
                            )
                            .authorizationGrantType(
                                    AuthorizationGrantType.AUTHORIZATION_CODE
                            )
                            .authorizationGrantType(
                                    AuthorizationGrantType.REFRESH_TOKEN
                            )
                            .redirectUri(
                                    "http://localhost:3000/callback"
                            )
                            .scope("openid")
                            .scope("profile")
                            .build();

            registeredClientRepository.save(
                    registeredClient
            );
        };
    }
}
