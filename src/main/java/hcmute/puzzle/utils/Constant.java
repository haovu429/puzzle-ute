package hcmute.puzzle.utils;

import hcmute.puzzle.exception.CustomException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

public class Constant {
  //  regex for email address
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+$";

  public static final String PASSWORD_REGEX = "^(?=.*[0-9])"
          + "(?=.*[a-z])(?=.*[A-Z])"
          + "(?=.*[@#$%^&+=])"
          + "(?=\\S+$).{6,}$";
  //  regex for phone number has 10 digits
  public static final String PHONE_REGEX = "^(\\+84|0)\\d{9,10}$";
  public static final Map<String, String> FILTER_PRODUCT =
      Map.ofEntries(
          Map.entry("m", "manufacturer_id"), Map.entry("c", "category"),
          Map.entry("name", "name "), Map.entry("min", "price"),
          Map.entry("max", "price"), Map.entry("os", "os"));

  @Value("${SMS_ACCESS_TOKEN}")
  public static String SMS_ACCESS_TOKEN;

  public static final String LOCAL_URL = "http://localhost:3000";
  public static final String ONLINE_URL = "https://puzzle-client-eight.vercel.app";
  public static final String ONLINE1_URL = "https://github.com/ngocdiem138/TLCN_PUZZLE";

  public static final String POSITION = "POSITION";
  public static final String SKILL = "SKILL";
  public static final String SERVICE = "SERVICE";

  public static final String SUFFIX_BLOG_IMAGE_FILE_NAME = "_blog_image"; // email_avatar

  public static final String SUFFIX_AVATAR_FILE_NAME = "_avatar"; // email_avatar

  public static final String SUFFIX_COMPANY_IMAGE_FILE_NAME = "_company_image"; // id_company_image

  public static final String SYSTEM_MAIL = "puzzle429@gmail.com";

  public static String validateTypeExtraInfo(String type) {
    if (type.equals(POSITION)) return POSITION;
    if (type.equals(SKILL)) return SKILL;
    if (type.equals(SERVICE)) return SERVICE;
    throw new CustomException("Type isn't exist");
  }

  public static final String[] LIST_HOST_FRONT_END =
      new String[] {LOCAL_URL, ONLINE_URL, ONLINE1_URL};

  public class StorageName {
    public static final String CLOUDINARY = "CLOUDINARY";
  }

  public class FileLocation {
    public static final String STORAGE_BLOG_IMAGE_LOCATION = "puzzle_ute/user/blog";

    public static final String STORAGE_IMAGE_LOCATION = "puzzle_ute/user/avatar";
    public static final String STORAGE_COMPANY_IMAGE_LOCATION = "puzzle_ute/company/image";
  }
}
