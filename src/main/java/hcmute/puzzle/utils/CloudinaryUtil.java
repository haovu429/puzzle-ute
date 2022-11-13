package hcmute.puzzle.utils;

import com.cloudinary.Cloudinary;


public class CloudinaryUtil {

  public static Cloudinary cloudinary;

  public static Cloudinary getCloudinary() {
    if (cloudinary == null) {
      // Set your Cloudinary credentials

      // Dotenv dotenv = Dotenv.load();
      cloudinary = new Cloudinary(System.getenv("CLOUDINARY_URL"));
      cloudinary.config.secure = true;
      System.out.println("Cloud name: " + cloudinary.config.cloudName);
    }
    return cloudinary;
  }
}
