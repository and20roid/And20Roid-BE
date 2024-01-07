package com.and20roid.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfiguration {

    @Value("${firebase.sdk.path}")
    private String firebaseSdkPath;

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(getFirebaseIs()))
                .build();

        FirebaseApp.initializeApp(options);
        return FirebaseAuth.getInstance(FirebaseApp.getInstance());
    }

    private InputStream getFirebaseIs() throws IOException {
        ClassPathResource resource = new ClassPathResource(firebaseSdkPath);
        if(resource.exists()) {
            return resource.getInputStream();
        } throw new RuntimeException("firebase 키가 존재하지 않습니다");
    }
}
