package com.nounou.times.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KeyGenerator {
    private KeyPair keyPair;

    public KeyGenerator() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération des clés RSA", e);
        }
    }

    public String getPrivateKeyPem() {
        String privateKeyB64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + privateKeyB64 + "\n-----END PRIVATE KEY-----";
    }

    public String getPublicKeyPem() {
        String publicKeyB64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + publicKeyB64 + "\n-----END PUBLIC KEY-----";
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}
