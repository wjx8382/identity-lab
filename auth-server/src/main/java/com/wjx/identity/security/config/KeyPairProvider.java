package com.wjx.identity.security.config;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class KeyPairProvider {

    private final KeyPair keyPair;

    public KeyPairProvider() {
        try {
            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance(
                            "RSA"
                    );

            generator.initialize(2048);

            this.keyPair =
                    generator.generateKeyPair();
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return (PrivateKey) keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return (PublicKey) keyPair.getPublic();
    }
}
