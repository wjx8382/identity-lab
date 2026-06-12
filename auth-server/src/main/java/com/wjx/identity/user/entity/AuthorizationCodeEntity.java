package com.wjx.identity.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "authorization_codes")
@Getter
@Setter
public class AuthorizationCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String username;

    private String clientId;

    private String codeChallenge;

    private String codeChallengeMethod;

    private LocalDateTime expireAt;

    private Boolean used;
}
