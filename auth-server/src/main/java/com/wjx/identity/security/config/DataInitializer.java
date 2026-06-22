package com.wjx.identity.security.config;

import com.wjx.identity.user.entity.ClientEntity;
import com.wjx.identity.user.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {

        ClientEntity client =
                clientRepository
                        .findByClientId("web-client")
                        .orElseGet(ClientEntity::new);

        client.setClientId("web-client");
        client.setClientSecret("123456");
        client.setRedirectUri(
                "http://localhost:3000/callback"
        );
        client.setScope("openid");
        client.setCertificateSubject(
                "CN=identity-lab-client"
        );

        clientRepository.save(client);
    }
}
