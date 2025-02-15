package com.nounou.times.security;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import java.util.Base64;

@ApplicationScoped
public class JwtConfigurer {
    
    @Inject
    KeyGenerator keyGenerator;

    void onStart(@Observes StartupEvent ev) {
        // Configure la clé publique pour la vérification JWT
        String publicKeyPem = keyGenerator.getPublicKeyPem();
        System.setProperty("mp.jwt.verify.publickey", publicKeyPem);
    }
}
