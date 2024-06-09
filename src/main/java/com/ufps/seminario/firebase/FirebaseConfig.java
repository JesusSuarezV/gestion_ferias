package com.ufps.seminario.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp  initializeFirebase() throws IOException {
        String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

        if (credentialsJson == null || credentialsJson.isEmpty()) {
            throw new IllegalStateException("Environment variable GOOGLE_APPLICATION_CREDENTIALS_JSON not set or empty.");
        }

        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();



        return FirebaseApp.initializeApp(options);
    }
}