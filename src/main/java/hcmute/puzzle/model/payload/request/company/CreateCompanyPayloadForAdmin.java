package hcmute.puzzle.model.payload.request.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CreateCompanyPayloadForAdmin {
    private String name;
    private String description;
    private MultipartFile image;
    private String website;
    private boolean isActive = false;
    private Long createdEmployerId = null;
}
