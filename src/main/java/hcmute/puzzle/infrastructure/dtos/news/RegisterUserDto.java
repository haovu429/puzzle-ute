package hcmute.puzzle.infrastructure.dtos.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.validation.Valid;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "constraint passes length more than six and contains at least one special character, number, uppercase, lowercase")
    private String password;
}
