package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CreateCompanyAdminRequest {
    private String name;
    private String description;
    private MultipartFile image;
    private String website;
    private Boolean isPublic;
    private Boolean isActive;
    private Long createdEmployerId;
}
