package hcmute.puzzle.test;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

public class TestCloudinary {

  public static void main(String[] args) {
    // Set your Cloudinary credentials

    // Dotenv dotenv = Dotenv.load();
    Cloudinary cloudinary = new Cloudinary(System.getenv("CLOUDINARY_URL"));
    cloudinary.config.secure = true;
    System.out.println("Cloud name: " + cloudinary.config.cloudName);

    try {
      // Upload the image
      Map params1 =
          ObjectUtils.asMap(
              "use_filename", true,
              "unique_filename", false,
              "overwrite", true);

      System.out.println(
          cloudinary
              .uploader()
              .upload(
                  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
                  params1));

      // Get the asset details
      Map params2 = ObjectUtils.asMap("quality_analysis", true);

      Map result = cloudinary.api().resource("coffee_cup", params2);

      String url = result.get("url").toString();

      String publicId = result.get("public_id").toString();

      System.out.println("url: " +url + " - public id: " +publicId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
