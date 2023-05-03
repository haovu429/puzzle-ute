package hcmute.puzzle.infrastructure.models.payload.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserPayload {

    @NotBlank(message = "Don't blank")
    @Size(min = 8, message = "username is not too short (8)")
    private String username;

    @Size(min = 8, max = 12, message = "phone must be between 8 and 32")
    private String phone;

    private String fullName;
}
