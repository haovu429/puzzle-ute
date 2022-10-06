package hcmute.puzzle.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class Constant {
  //  regex for email address
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+$";
  //  regex for phone number has 10 digits
  public static final String PHONE_REGEX = "^(\\+84|0)\\d{9,10}$";
  public static final Map<String, String> FILTER_PRODUCT =
      Map.ofEntries(
          Map.entry("m", "manufacturer_id"), Map.entry("c", "category"),
          Map.entry("name", "name "), Map.entry("min", "price"),
          Map.entry("max", "price"), Map.entry("os", "os"));
  @Value("${SMS_ACCESS_TOKEN}")
  public static String SMS_ACCESS_TOKEN;
}
