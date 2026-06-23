package com.wjx.identity.oauth2.controller;

import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.oauth2.dto.ClientRegistrationRequest;
import com.wjx.identity.oauth2.dto.ClientRegistrationResponse;
import com.wjx.identity.user.entity.ClientEntity;
import com.wjx.identity.user.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class ClientRegistrationController {

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ClientRegistrationResponse register(
            @RequestBody ClientRegistrationRequest request
    ) {
        if (request.client_id() == null
                || request.client_id().isBlank()) {
            throw new BusinessException("Client ID is required");
        }

        if (request.client_secret() == null
                || request.client_secret().isBlank()) {
            throw new BusinessException("Client secret is required");
        }

        if (request.redirect_uri() == null
                || request.redirect_uri().isBlank()) {
            throw new BusinessException("Redirect URI is required");
        }

        if (request.scope() == null
                || request.scope().isBlank()) {
            throw new BusinessException("Scope is required");
        }

        if (request.certificate_subject() == null
                || request.certificate_subject().isBlank()) {
            throw new BusinessException("Certificate subject is required");
        }

        if (clientRepository.findByClientId(
                request.client_id()
        ).isPresent()) {
            throw new BusinessException("Client already exists");
        }

        if (clientRepository.findByCertificateSubject(
                request.certificate_subject()
        ).isPresent()) {
            throw new BusinessException(
                    "Client certificate already exists"
            );
        }

        ClientEntity client = new ClientEntity();

        client.setClientId(request.client_id());
        client.setClientSecret(
                passwordEncoder.encode(request.client_secret())
        );
        client.setRedirectUri(request.redirect_uri());
        client.setScope(request.scope());
        client.setCertificateSubject(
                request.certificate_subject()
        );

        ClientEntity saved =
                clientRepository.save(client);

        return new ClientRegistrationResponse(
                saved.getClientId(),
                saved.getRedirectUri(),
                saved.getScope(),
                saved.getCertificateSubject()
        );
    }
}
