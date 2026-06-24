package com.wjx.sas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.UUID;

@Configuration
public class RegisteredClientConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("web-client")
                        .clientSecret("{noop}123456")
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                        )
                        .authorizationGrantType(
                                AuthorizationGrantType.AUTHORIZATION_CODE
                        )
                        .authorizationGrantType(
                                AuthorizationGrantType.REFRESH_TOKEN
                        )
                        .redirectUri("http://localhost:3000/callback")
                        .scope("openid")
                        .scope("profile")
                        .build();

        return new InMemoryRegisteredClientRepository(
                registeredClient
        );
    }
}
