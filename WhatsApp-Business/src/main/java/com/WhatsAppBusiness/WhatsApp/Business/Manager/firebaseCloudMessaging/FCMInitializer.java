package com.WhatsAppBusiness.WhatsApp.Business.Manager.firebaseCloudMessaging;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class FCMInitializer {

    static String FIREBASE_CONFIG_PATH = System.getenv().getOrDefault("FIREBASE_CONFIG_PATH", "");

//    public FCMInitializer() {
//        initialize();
//    }

    Logger LOGGER = LoggerFactory.getLogger(FCMInitializer.class);

//    @PostConstruct
//    public void initialize() {
//        try {
//            LOGGER.info("Initializing Firebase");
//            File file = new File(FIREBASE_CONFIG_PATH);
//            if (!file.exists()) {
//                LOGGER.error("Firebase configuration file not found at: {}", FIREBASE_CONFIG_PATH);
//                throw new FileNotFoundException("Firebase configuration file not found at: " + FIREBASE_CONFIG_PATH);
//            }
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(file)))
//                    .build();
//            LOGGER.info("Firebase configuration file found at: {}", FIREBASE_CONFIG_PATH);
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                LOGGER.info("Firebase application initialized");
//            }
//        } catch (IOException e) {
//            LOGGER.error("Error reading Firebase configuration file: {}", FIREBASE_CONFIG_PATH, e);
//        } catch (Exception e) {
//            LOGGER.error("Error initializing Firebase: {}", e.getMessage());
//        }
//    }

}