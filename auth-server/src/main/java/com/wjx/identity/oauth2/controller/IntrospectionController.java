package com.wjx.identity.oauth2.controller;

import com.wjx.identity.oauth2.dto.IntrospectRequest;
import com.wjx.identity.oauth2.dto.IntrospectResponse;
import com.wjx.identity.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class IntrospectionController {

    private final JwtService jwtService;

    @PostMapping("/introspect")
    public IntrospectResponse introspect(
            @RequestBody IntrospectRequest request
    ) {
        try {
            Claims claims =
                    jwtService.parseClaims(
                            request.token()
                    );

            return new IntrospectResponse(
                    true,
                    claims.getSubject(),
                    claims.get(
                            "scope",
                            String.class
                    ),
                    claims.getExpiration()
                            .toInstant()
                            .getEpochSecond()
            );
        } catch (Exception e) {
            return new IntrospectResponse(
                    false,
                    null,
                    null,
                    null
            );
        }
    }
}
