package hcmute.puzzle.utils.firebase.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;

//@Service
public class FCMInitializer {
  @Value("${app.firebase-configuration-file}")
  private String firebaseConfigPath;

  Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

  @PostConstruct
  public void initialize() {
    try {
      //            FirebaseOptions options = new FirebaseOptions.Builder()
      //                    .setCredentials(GoogleCredentials.fromStream(new
      // ClassPathResource(firebaseConfigPath).getInputStream())).build();

        //FirebaseOptions.Builder builder = FirebaseOptions.builder();

      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .setDatabaseUrl(firebaseConfigPath)
              .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
        logger.info("Firebase application has been initialized");
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }
}
