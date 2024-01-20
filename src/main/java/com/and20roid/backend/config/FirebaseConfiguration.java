package com.and20roid.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
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
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) throws IOException {
        return FirebaseAuth.getInstance(FirebaseApp.getInstance());
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(getFirebaseIs()))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    private InputStream getFirebaseIs() throws IOException {
        ClassPathResource resource = new ClassPathResource(firebaseSdkPath);
        if(resource.exists()) {
            return resource.getInputStream();
        } throw new RuntimeException("firebase 키가 존재하지 않습니다");
    }
}
