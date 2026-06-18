package com.wjx.identity.oauth2.controller;

import com.wjx.identity.auth.dto.RevokeRequest;
import com.wjx.identity.user.entity.RefreshTokenEntity;
import com.wjx.identity.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class RevocationController {

    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/revoke")
    public void revoke(
            @RequestBody RevokeRequest request
    ) {
        RefreshTokenEntity token =
                refreshTokenRepository
                        .findByToken(
                                request.token()
                        )
                        .orElseThrow(
                                () -> new RuntimeException("Token not found")
                        );
        token.setRevoked(true);

        refreshTokenRepository.save(token);
    }
}
