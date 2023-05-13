package hcmute.puzzle.infrastructure.dtos.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import hcmute.puzzle.utils.Constant;
import lombok.*;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserForAdminDto {
    @JsonProperty("username")
    @NotBlank(message = "Don't blank")
    @Size(min = 8, message = "username is not too short (8)")
    private String username;

    @NotBlank
    @Size(min = 6, message = "constraint passes length more than six and ")
    @Pattern(regexp = Constant.PASSWORD_REGEX, message = "Password contains at least one special character, number, uppercase, " +
            "lowercase")
    private String password;

    @JsonProperty("phone")
    @Size(min = 8, max = 12, message = "phone must be between 8 and 32")
    private String phone;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("emailVerified")
    private Boolean emailVerified;

//    @JsonProperty("locale")
//    private String locale;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("provider")
    private String provider;

    @Builder.Default
    @JsonProperty("roleCodes")
    private List<String> roleCodes = new ArrayList<>();
}
