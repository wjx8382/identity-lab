package com.wjx.identity.oauth2.controller;

import com.wjx.identity.oauth2.dto.OAuth2TokenResponse;
import com.wjx.identity.oauth2.dto.OauthRequest;
import com.wjx.identity.oauth2.dto.OAuthTokenRequest;
import com.wjx.identity.common.exception.BusinessException;
import com.wjx.identity.common.util.CodeGenerator;
import com.wjx.identity.oauth2.dto.UserInfoResponse;
import com.wjx.identity.oauth2.repository.ParRequestRepository;
import com.wjx.identity.oauth2.service.PkceService;
import com.wjx.identity.oauth2.service.ScopeValidator;
import com.wjx.identity.security.jwt.JwtService;
import com.wjx.identity.security.util.CertificateUtils;
import com.wjx.identity.user.entity.*;
import com.wjx.identity.user.repository.AuthorizeCodeRepository;
import com.wjx.identity.user.repository.ClientRepository;
import com.wjx.identity.user.repository.RefreshTokenRepository;
import com.wjx.identity.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

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

    private final ScopeValidator scopeValidator;

    private final ParRequestRepository parRequestRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize(
            @ModelAttribute OauthRequest oauthRequest,
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {
            throw new BusinessException(
                    "Authentication required"
            );
        }

        if (oauthRequest.request_uri() == null
                || oauthRequest.request_uri().isBlank()
        ) {

            throw new BusinessException(
                    "request_uri required"
            );
        }

        ParRequestEntity parRequest =
                parRequestRepository.findByRequestId(
                        oauthRequest.request_uri()
                )
                .orElseThrow(
                        () -> new BusinessException("PAR request not found")
                );

        if (parRequest.getExpireAt()
                .isBefore(LocalDateTime.now()))  {
            throw new BusinessException(
                    "PAR request has expired"
            );
        }

        String code = codeGenerator.generateCode();

        AuthorizationCodeEntity entity = new AuthorizationCodeEntity();

        entity.setCode(code);

        entity.setClientId(
                parRequest.getClientId()
        );

        entity.setUsername(
                authentication.getName()
        );

        entity.setScope(
                parRequest.getScope()
        );

        entity.setCodeChallenge(
                parRequest.getCodeChallenge()
        );

        entity.setCodeChallengeMethod(
                parRequest.getCodeChallengeMethod()
        );

        entity.setExpireAt(
                LocalDateTime.now().plusMinutes(5)
        );

        entity.setUsed(false);

        authorizeCodeRepository.save(entity);

        parRequestRepository.delete(parRequest);

        String response = jwtService.generateJarmResponse(
                code,
                parRequest.getState(),
                parRequest.getClientId()
        );

        String location =
                parRequest.getRedirectUri()
                        + "?response="
                        + URLEncoder.encode(
                                response,
                                StandardCharsets.UTF_8
                        );

        if (parRequest.getState() != null) {

            location += "&state="
                    + URLEncoder.encode(
                            parRequest.getState(),
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
            @RequestBody OAuthTokenRequest request,
            HttpServletRequest httpRequest
    ) throws NoSuchAlgorithmException {
        String certificateSubject =
                CertificateUtils.getClientCertificateSubject(
                        httpRequest
                );

        if (certificateSubject == null) {
            throw new BusinessException(
                    "Client certificate required"
            );
        }

        ClientEntity client =
                clientRepository
                        .findByCertificateSubject(
                                certificateSubject
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Client certificate not trusted"
                                )
                        );

        if (!client.getClientId()
                .equals(request.client_id())) {

            throw new BusinessException(
                    "Client id mismatch"
            );
        }

        if (!passwordEncoder.matches(
                request.client_secret(),
                client.getClientSecret()
        )) {

            throw new BusinessException(
                    "Invalid client secret"
            );
        }

        AuthorizationCodeEntity codeEntity =
                authorizeCodeRepository
                        .findByCode(
                                request.code()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "code not found"
                                )
                        );

        if (Boolean.TRUE.equals(
                codeEntity.getUsed()
        )) {
            throw new BusinessException(
                    "code has used"
            );
        }

        if (codeEntity.getExpireAt()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new BusinessException(
                    "code has expired"
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
                    "PKCE verification failed"
            );
        }

        UserEntity user =
                userRepository
                        .findByUsername(
                                codeEntity.getUsername()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "User not found"
                                )
                        );

        String accessToken =
                jwtService.generateAccessToken(
                        user,
                        codeEntity.getScope()
                );

        String refreshToken =
                jwtService.generateRefreshToken(user);

        RefreshTokenEntity refreshTokenEntity =
                new RefreshTokenEntity();

        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUsername(user.getUsername());
        refreshTokenEntity.setClientId(codeEntity.getClientId());
        refreshTokenEntity.setExpireAt(
                LocalDateTime.now().plusDays(7)
        );
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

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
}
