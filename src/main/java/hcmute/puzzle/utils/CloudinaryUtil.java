package hcmute.puzzle.utils;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryUtil {

  public static Cloudinary cloudinary;

  @Autowired
  private Environment environment;

  public Cloudinary getCloudinary() {
    if (cloudinary == null) {
      // Set your Cloudinary credentials

      // Dotenv dotenv = Dotenv.load();
      cloudinary = new Cloudinary(environment.getProperty("CLOUDINARY_URL"));
      cloudinary.config.secure = true;
      System.out.println("Cloud name: " + cloudinary.config.cloudName);
    }
    return cloudinary;
  }
}
