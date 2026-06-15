package com.wjx.identity.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ParRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String requestId;

    private String clientId;

    private String redirectUri;

    private String scope;

    private String state;

    private String codeChallenge;

    private String codeChallengeMethod;

    private LocalDateTime expireAt;
}
