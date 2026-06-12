package com.wjx.identity.oauth2.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PkceService {

    public String generateChallenge(
            String codeVerifier
    ) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(codeVerifier.getBytes());

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
    }
}
