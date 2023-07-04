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

  public static final String SUFFIX_CV_IMAGE_FILE_NAME = "_company_image"; // id_company_image
  public static final String SUFFIX_CATEGORY_IMAGE_FILE_NAME = "category_image"; // id_company_image

  public static final String SUFFIX_BLOG_POST_THUMBNAIL = "_blogpost_thumbnail"; // id_company_image
  public static final String SYSTEM_MAIL = "puzzleute@gmail.com";

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
    public static final String STORAGE_BLOG_THUMBNAIL_LOCATION = "puzzle_ute/user/blog/thumbnail";
    public static final String STORAGE_IMAGE_LOCATION = "puzzle_ute/user/avatar";
    public static final String STORAGE_COMPANY_IMAGE_LOCATION = "puzzle_ute/company/image";
    public static final String STORAGE_CATEGORY_IMAGE_LOCATION = "puzzle_ute/category/image";

    public static final String STORAGE_CATEGORY_CV_LOCATION = "puzzle_ute/application/cv";
  }

  public class AuthPath {
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String LOGIN_GOOGLE_URL = "/login-google";
    public static final String FORGOT_PASSWORD_URL = "/forgot-password";
    public static final String RESET_PASSWORD_URL = "/reset-password";
    public static final String RESEND_VERIFY_TOKEN_URL = "/resend-mail/verify-account";
    public static final String VERIFY_ACCOUNT_URL = "/verify-account";
  }

  public class Hirize {
    public static final String HIRIZE_BALANCE = "HIRIZE_BALANCE";
    public static final String HIRIZE_HIRIZE_IQ_API_KEY = "HIRIZE_HIRIZE_IQ_API_KEY";
    public static final String HIRIZE_AI_MATCHER_API_KEY = "HIRIZE_AI_MATCHER_API_KEY";
    public static final String HIRIZE_RESUME_PARSER_API_KEY = "HIRIZE_RESUME_PARSER_API_KEY";

    public static final String HIRIZE_JOB_PARSER_API_KEY = "HIRIZE_JOB_PARSER_API_KEY";
  }

  public class Cohere {
    public static final String COHERE_API_KEY = "COHERE_API_KEY";
  }

  public class Translate {
    public static final String TRANSLATE_END_POINT = "TRANSLATE_END_POINT";
  }

  public class DetectLanguage {
    public static final String DETECT_LANGUAGE_API_KEY = "DETECT_LANGUAGE_API_KEY";

    public static final String DETECT_LANGUAGE_ENABLE = "DETECT_LANGUAGE_ENABLE";
  }

  public class ResponseCode {
    public static final int STATUS_OK = 200;
    public static final int STATUS_NO_CONTENT = 200;
    public static final int STATUS_BAD = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_NOT_AGAIN = 405;
    public static final int STATUS_RUNTIME_EXCEPTION = 500;
    public static final int STATUS_CUSTOM_EXCEPTION = 501;
  }

  public class ResponseMessage {
    public static final String MSG_CUSTOM_EXCEPTION = "CUSTOM_EXCEPTION";
    //public static final int STATUS_

    public static final String CODE_ERROR_NOT_FOUND = "404";
    public static final String MSG_ERROR_NOT_FOUND = "ERROR_NOT_FOUND";
    public static final String CODE_ERROR_FORBIDDEN = "403";
    public static final String MSG_ERROR_FORBIDDEN = "ERROR_FORBIDDEN";
    public static final String CODE_ERROR_NOT_AGAIN = "405";
    public static final String MSG_ERROR_NOT_AGAIN = "ERROR_NOT_AGAIN";
    public static final String CODE_ERROR_INACTIVE = "406";
    public static final String MSG_ERROR_INACTIVE = "ERROR_INACTIVE";

    public static final String MSG_ERROR_SERVER = "ERROR_SERVER";
    public static final String MSG_ERROR_CLIENT = "ERROR_CLIENT";

    public static final String MSG_ERROR_MISSING_PARAMS = "ERROR_MISSING_PARAMS";
  }
}
