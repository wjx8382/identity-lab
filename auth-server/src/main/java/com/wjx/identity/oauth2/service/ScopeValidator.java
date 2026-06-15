package com.wjx.identity.oauth2.service;

import com.wjx.identity.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ScopeValidator {

    public void validate(
            String requestedScope,
            String clientScope
    ) {
        if (requestedScope == null
                || requestedScope.isBlank()) {
            throw new BusinessException(
                    "scope required"
            );
        }

        if (clientScope == null
                || clientScope.isBlank()) {
            throw new BusinessException(
                    "client scope not configured"
            );
        }

        Set<String> requested =
                Set.of(
                        requestedScope.split("\\s+")
                );

        Set<String> allowed =
                Set.of(
                        clientScope.split("\\s+")
                );

        if (!allowed.containsAll(
                requested
        )) {

            throw new BusinessException(
                    "scope not allowed"
            );
        }
    }
}
