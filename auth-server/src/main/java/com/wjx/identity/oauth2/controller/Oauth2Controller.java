package com.wjx.identity.oauth2.controller;

import com.wjx.identity.oauth2.dto.OAuth2TokenResponse;
import com.wjx.identity.oauth2.dto.OauthRequest;
import com.wjx.identity.oauth2.dto.OAuthTokenRequest;
import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.common.util.CodeGenerator;
import com.wjx.identity.oauth2.dto.UserInfoResponse;
import com.wjx.identity.oauth2.service.PkceService;
import com.wjx.identity.security.jwt.JwtService;
import com.wjx.identity.user.entity.AuthorizationCodeEntity;
import com.wjx.identity.user.entity.ClientEntity;
import com.wjx.identity.user.entity.UserEntity;
import com.wjx.identity.user.repository.AuthorizeCodeRepository;
import com.wjx.identity.user.repository.ClientRepository;
import com.wjx.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class Oauth2Controller {

    private final ClientRepository clientRepository;

    private final AuthorizeCodeRepository authorizeCodeRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PkceService pkceService;

    private final CodeGenerator codeGenerator;

    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize(
            @ModelAttribute OauthRequest oauthRequest,
            Authentication authentication
    ) {

        if (authentication ==  null) {
            throw new BusinessException(
                    "未登录"
            );
        }

        if (oauthRequest.code_challenge() == null) {
            throw new BusinessException(
                    "PKCE required"
            );
        }

        if (!"S256".equals(
                oauthRequest.code_challenge_method()
        )) {
            throw new BusinessException(
                    "Only S256 supported"
            );
        }

        ClientEntity client =
                clientRepository
                        .findByClientId(oauthRequest.client_id())
                        .orElseThrow(
                                () -> new BusinessException("Client不存在")
                        );

        if (!client.getRedirectUri()
                .equals(oauthRequest.redirect_uri())) {
            throw new BusinessException(
                    "redirect_uri不匹配"
            );
        }

        validateScope(
                oauthRequest.scope(),
                client.getScope()
        );

        String code = codeGenerator.generateCode();

        AuthorizationCodeEntity entity = new AuthorizationCodeEntity();

        entity.setCode(code);

        entity.setClientId(
                oauthRequest.client_id()
        );

        entity.setUsername(
                authentication.getName()
        );

        entity.setScope(
                oauthRequest.scope()
        );

        entity.setCodeChallenge(
                oauthRequest.code_challenge()
        );

        entity.setCodeChallengeMethod(
                oauthRequest.code_challenge_method()
        );

        entity.setExpireAt(
                LocalDateTime.now().plusMinutes(5)
        );

        entity.setUsed(false);

        authorizeCodeRepository.save(entity);

        String location =
                oauthRequest.redirect_uri()
                        + "?code="
                        + code;

        if (oauthRequest.state() != null) {

            location += "&state="
                    + URLEncoder.encode(
                            oauthRequest.state(),
                            StandardCharsets.UTF_8
            );
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,
                        location)
                .build();
    }

    @PostMapping("/token")
    public OAuth2TokenResponse token(
            @RequestBody OAuthTokenRequest request
    ) throws NoSuchAlgorithmException {
        ClientEntity client =
                clientRepository
                        .findByClientId(
                                request.client_id()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Client不存在"
                                )
                        );

        if (!client.getClientSecret()
                .equals(request.client_secret())) {

            throw new BusinessException(
                    "client_secret错误"
            );
        }

        AuthorizationCodeEntity codeEntity =
                authorizeCodeRepository
                        .findByCode(
                                request.code()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "code不存在"
                                )
                        );

        if (Boolean.TRUE.equals(
                codeEntity.getUsed()
        )) {
            throw new BusinessException(
                    "code已使用"
            );
        }

        if (codeEntity.getExpireAt()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new BusinessException(
                    "code已过期"
            );
        }

        String challennge =
                pkceService.generateChallenge(
                        request.code_verifier()
                );

        if (!challennge.equals(
                codeEntity.getCodeChallenge()
        )) {

            throw new BusinessException(
                    "PKCE验证失败"
            );
        }

        UserEntity user =
                userRepository
                        .findByUsername(
                                codeEntity.getUsername()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "用户不存在"
                                )
                        );

        String accessToken =
                jwtService.generateAccessToken(
                        user,
                        codeEntity.getScope()
                );

        String refreshToken =
                jwtService.generateRefreshToken(user);

        String idToken = null;
        if (codeEntity.getScope()
                .contains("openid")) {

            idToken =
                    jwtService.generateIdToken(
                            user,
                            client.getClientId()
                    );
        }

        codeEntity.setUsed(true);

        authorizeCodeRepository.save(codeEntity);

        return new OAuth2TokenResponse(
                accessToken,
                refreshToken,
                idToken,
                "Bearer",
                900L
        );
    }

    @GetMapping("/userinfo")
    public UserInfoResponse userinfo() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String token =
                (String) authentication.getDetails();

        String scope =
                jwtService.extractScope(token);

        if (scope == null
                || !scope.contains("openid")) {

            throw new BusinessException(
                    "openid scope required"
            );
        }

        String username = authentication.getName();

        UserEntity user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();

        return new UserInfoResponse(
                username,
                username,
                user.getEmail()
        );
    }

    private void validateScope(
            String requestedScope,
            String clientScope
    ) {

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
