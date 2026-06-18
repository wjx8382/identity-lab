package com.wjx.identity.auth.controller;

import com.wjx.identity.auth.dto.*;
import com.wjx.identity.auth.service.AuthService;
import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.security.jwt.JwtService;
import com.wjx.identity.user.entity.RefreshTokenEntity;
import com.wjx.identity.user.entity.UserEntity;
import com.wjx.identity.user.repository.RefreshTokenRepository;
import com.wjx.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginReponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/test-token")
    public String testToken(@RequestParam String token) {
        return jwtService.extractUsername(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        String refreshToken = request.refreshToken();

        RefreshTokenEntity refreshTokenEntity =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Token not found"
                                )
                        );

        if (refreshTokenEntity.getRevoked()) {

            throw new BusinessException(
                    "Token revoked"
            );
        }

        if (!jwtService.validateToken(refreshToken)) {
            throw new BusinessException(
                    "Invalid refresh token"
            );
        }

        String username =
                jwtService.extractUsername(
                        refreshToken
                );

        UserEntity user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();

        String accessToken =
                jwtService.generateAccessToken(
                        user
                );

        return new TokenResponse(
                accessToken
        );
    }
}
