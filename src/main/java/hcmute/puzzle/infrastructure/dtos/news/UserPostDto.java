package hcmute.puzzle.infrastructure.dtos.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import hcmute.puzzle.infrastructure.entities.Auditable;
import hcmute.puzzle.utils.Constant;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserPostDto extends Auditable {

  @JsonProperty("id")
  private long id;

  @JsonProperty("username")
  // @NotBlank(message = "Don't blank")
  // @Size(min = 8, message = "username is not too short (8)")
  private String username;

  @Email
  @NotBlank
  @JsonProperty("email")
  @Pattern(regexp = Constant.EMAIL_REGEX, message = "Email is invalid")
  private String email;

  @JsonProperty("phone")
  @Size(min = 8, max = 12, message = "phone must be between 8 and 32")
  private String phone;

  @JsonProperty("avatar")
  private String avatar;

  @JsonProperty("fullName")
  private String fullName;

  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z", timezone = "Asia/Ho_Chi_Minh")
  @JsonProperty("lastLoginAt")
  private Date lastLoginAt;

  @JsonProperty("emailVerified")
  private boolean emailVerified;

  @JsonProperty("locale")
  private String locale;

  @JsonProperty("isActive")
  private boolean isActive = true;

  private String provider;

  @JsonProperty("roleCodes")
  private List<String> roleCodes = new ArrayList<>();
}
