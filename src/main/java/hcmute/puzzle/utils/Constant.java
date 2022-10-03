package hcmute.puzzle.utils;

import java.util.Map;

public class Constant {
  //  regex for email address
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+$";
  //  regex for phone number has 10 digits
  public static final String PHONE_REGEX = "^(\\+84|0)\\d{9,10}$";

  public static final String SMS_ACCESS_TOKEN = "VLXmAh7BckpeiJUzXMu5NCrhanYPKTtw";

  public static final Map<String, String> FILTER_PRODUCT =
      Map.ofEntries(
          Map.entry("m", "manufacturer_id"), Map.entry("c", "category"),
          Map.entry("name", "name "), Map.entry("min", "price"),
          Map.entry("max", "price"), Map.entry("os", "os"));
}
