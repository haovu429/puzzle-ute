package hcmute.puzzle.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hcmute.puzzle.utils.Constant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
  private long id;

  @NotBlank(message = "Don't blank")
  @Size(min = 8, message = "username is not too short (8)")
  private String username;

  @Pattern(regexp = Constant.EMAIL_REGEX, message = "Email is invalid")
  private String email;

  @NotBlank(message = "Don't blank")
  @Size(min = 8, max = 32, message = "Password must be between 8 and 32")
  private String password;

  @Size(min = 8, max = 12, message = "phone must be between 8 and 32")
  private String phone;

  private String avatar;

  private Boolean isOnline;

  private Date joinDate;

  private Date lastOnline;

  private boolean isActive;

  private List<String> roleCodes = new ArrayList<>();
}