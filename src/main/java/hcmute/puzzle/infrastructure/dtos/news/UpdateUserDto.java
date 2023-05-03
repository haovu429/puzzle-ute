package hcmute.puzzle.infrastructure.dtos.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import hcmute.puzzle.utils.Constant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class UpdateUserDto {
    @JsonProperty("username")
    @NotBlank(message = "Don't blank")
    @Size(min = 8, message = "username is not too short (8)")
    private String username;

    @JsonProperty("phone")
    @Size(min = 8, max = 12, message = "phone must be between 8 and 32")
    private String phone;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("fullName")
    private String fullName;
}
