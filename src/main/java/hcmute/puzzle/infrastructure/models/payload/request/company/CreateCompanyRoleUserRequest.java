package hcmute.puzzle.infrastructure.models.payload.request.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CreateCompanyRoleUserRequest {
    private String name;
    private String description;
    private MultipartFile imageFile;
    private String website;
    private Boolean isPublic;
}
