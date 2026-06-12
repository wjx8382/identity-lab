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

        if (clientRepository
                .findByClientId("web-client")
                .isPresent()) {
            return;
        }

        ClientEntity client =
                new ClientEntity();

        client.setClientId("web-client");
        client.setClientSecret("123456");
        client.setRedirectUri(
                "http://localhost:3000/callback"
        );
        client.setScope("openid");

        clientRepository.save(client);
    }
}
