package com.wjx.identity.security.jwt;

import com.wjx.identity.security.config.KeyPairProvider;
import com.wjx.identity.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${identity.issuer}")
    private String issuer;

    private SecretKey secretKey;

    private final KeyPairProvider keyPairProvider;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(UserEntity user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(
                        "role",
                        user.getRole().getRoleName()
                )
                .claim("token_type", "login")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )
//                .signWith(secretKey)
                .signWith(
                        keyPairProvider.getPrivateKey(),
                        Jwts.SIG.RS256
                )
                .compact();
    }

    public String generateAccessToken(
            UserEntity user,
            String scope
    ) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(
                        "role",
                        user.getRole().getRoleName()
                )
                .claim("token_type", "oauth2")
                .claim("scope", scope)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )
//                .signWith(secretKey)
                .signWith(
                        keyPairProvider.getPrivateKey(),
                        Jwts.SIG.RS256
                )
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )
//                .signWith(secretKey)
                .signWith(
                        keyPairProvider.getPrivateKey(),
                        Jwts.SIG.RS256
                )
                .compact();
    }

    public String generateIdToken(
            UserEntity user,
            String clientId
    ) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim(
                        "email",
                        user.getEmail()
                )
                .claim(
                        "name",
                        user.getUsername()
                )
                .claim(
                        "iss",
                        issuer
                )
                .claim(
                        "aud",
                        clientId
                )
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )
//                .signWith(secretKey)
                .signWith(
                        keyPairProvider.getPrivateKey(),
                        Jwts.SIG.RS256
                )
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(
                        keyPairProvider.getPublicKey()
                )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(String token) {
        return getClaims(token)
                .get("role", String.class);
    }

    public String extractScope(String token) {
        return getClaims(token)
                .get("scope", String.class);
    }
}
