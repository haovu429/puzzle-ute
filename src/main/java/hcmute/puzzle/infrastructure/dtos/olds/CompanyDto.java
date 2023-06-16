package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
  private Long id;
  @NotEmpty
  private String name;
  private String description;
  private String image;
  private MultipartFile imageFile;
  private String website;
  private Boolean isPublic;
  private Boolean isActive;
  private Long createdEmployerId;
}
