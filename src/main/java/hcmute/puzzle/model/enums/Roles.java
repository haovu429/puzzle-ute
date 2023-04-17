package hcmute.puzzle.utils;

public enum Roles {
  ADMIN("ADMIN"),
  USER("USER"),
  GUEST("GUEST"),
  CANDIDATE("CANDIDATE"),
  EMPLOYER("EMPLOYER");

  public final String value;

  Roles(String value) {
    this.value = value;
  }
}
