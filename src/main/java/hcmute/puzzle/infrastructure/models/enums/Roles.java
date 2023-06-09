package hcmute.puzzle.infrastructure.models.enums;

public enum Roles {
  ADMIN("ADMIN"),
  USER("USER"),
  GUEST("GUEST"),
  CANDIDATE("CANDIDATE"),
  EMPLOYER("EMPLOYER");

  private final String value;

  public String getValue() {
    return value;
  }
  Roles(String value) {
    this.value = value;
  }
}