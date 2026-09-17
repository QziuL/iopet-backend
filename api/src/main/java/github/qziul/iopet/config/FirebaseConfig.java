package github.qziul.iopet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path:classpath:firebase-service-account.json}")
    private String credentialsPath;

    @Bean
    public FirebaseMessaging firebaseMessaging(ResourceLoader resourceLoader) {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseMessaging.getInstance();
        }

        try {
            Resource resource = resourceLoader.getResource(credentialsPath);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(is))
                            .build();
                    FirebaseApp app = FirebaseApp.initializeApp(options);
                    log.info("FirebaseApp inicializado com sucesso via arquivo de credenciais: {}", credentialsPath);
                    return FirebaseMessaging.getInstance(app);
                }
            } else {
                log.warn("Arquivo de credenciais do Firebase não encontrado em '{}'. Tentando credenciais padrão do ambiente (Application Default Credentials)...", credentialsPath);
                try {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
                    FirebaseApp app = FirebaseApp.initializeApp(options);
                    log.info("FirebaseApp inicializado com sucesso via Application Default Credentials.");
                    return FirebaseMessaging.getInstance(app);
                } catch (IOException ex) {
                    log.warn("Credenciais padrão do Google/Firebase não foram encontradas no ambiente: {}. O serviço de notificações funcionará em modo fallback.", ex.getMessage());
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao inicializar o Firebase: {}", e.getMessage(), e);
            return null;
        }
    }
}
