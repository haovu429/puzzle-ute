package hcmute.puzzle.infrastructure.models.payload.request.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CreateCompanyDto {
    private String name;
    private String description;
    private MultipartFile image = null;
    private String website;
}
