package hcmute.puzzle.login_google;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GooglePojo {
  private String id;
  private String email;
  private boolean verified_email;
  private String name;
  private String given_name;
  private String family_name;
  private String link;
  private String picture;
  private String locale;

  @Override
  public String toString() {
    return "GooglePojo{"
        + "id='"
        + id
        + '\''
        + ", email='"
        + email
        + '\''
        + ", verified_email="
        + verified_email
        + ", name='"
        + name
        + '\''
        + ", given_name='"
        + given_name
        + '\''
        + ", family_name='"
        + family_name
        + '\''
        + ", link='"
        + link
        + '\''
        + ", picture='"
        + picture
        + '\''
        + ", locale='"
        + locale
        + '\''
        + '}';
  }
}
