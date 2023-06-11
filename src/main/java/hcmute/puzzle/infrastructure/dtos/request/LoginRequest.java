package hcmute.puzzle.infrastructure.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import hcmute.puzzle.utils.Constant;
import lombok.Data;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Data
public class LoginRequest {

    @Email
    @NotBlank
    @JsonProperty("email")
    @Pattern(regexp = Constant.EMAIL_REGEX, message = "Email is invalid")
    String email;

    @NotBlank
    String password;
}
