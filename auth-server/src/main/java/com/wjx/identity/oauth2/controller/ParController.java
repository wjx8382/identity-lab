package com.wjx.identity.oauth2.controller;

import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.oauth2.dto.ParRequest;
import com.wjx.identity.oauth2.dto.ParResponse;
import com.wjx.identity.oauth2.repository.ParRequestRepository;
import com.wjx.identity.oauth2.service.ScopeValidator;
import com.wjx.identity.user.entity.ClientEntity;
import com.wjx.identity.user.entity.ParRequestEntity;
import com.wjx.identity.user.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2/par")
@RequiredArgsConstructor
public class ParController {

    private final ParRequestRepository parRequestRepository;

    private final ClientRepository clientRepository;

    private final ScopeValidator scopeValidator;

    @PostMapping
    public ParResponse par(
            @RequestBody ParRequest request
    ) {

        ClientEntity client =
                clientRepository.findByClientId(
                        request.client_id()
                ).orElseThrow(
                        () -> new BusinessException("Client not found")
                );

        if (!client.getRedirectUri()
                .equals(request.redirect_uri()
        )) {
            throw new BusinessException("Redirect URI mismatch");
        }

        scopeValidator.validate(
                request.scope(),
                client.getScope()
        );

        if (request.code_challenge() == null
                || request.code_challenge().isBlank()
        ) {

            throw new BusinessException(
                    "PKCE required"
            );
        }

        if (!"S256".equals(
                request.code_challenge_method()
        )) {

            throw new BusinessException(
                    "Only S256 supported"
            );
        }

        ParRequestEntity entity = new ParRequestEntity();
        String requestId =
                "urn:par:" + UUID.randomUUID();
        entity.setRequestId(requestId);
        entity.setClientId(request.client_id());
        entity.setRedirectUri(request.redirect_uri());
        entity.setScope(request.scope());
        entity.setState(request.state());
        entity.setCodeChallenge(request.code_challenge());
        entity.setCodeChallengeMethod(request.code_challenge_method());
        entity.setExpireAt(
                LocalDateTime.now().plusMinutes(5)
        );

        parRequestRepository.save(entity);

        return new ParResponse(
                entity.getRequestId(),
                300L
        );
    }
}
