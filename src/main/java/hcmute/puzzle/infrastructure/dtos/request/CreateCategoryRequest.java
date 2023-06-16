package hcmute.puzzle.infrastructure.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.annotation.XmlRootElement;

@Data
//@XmlRootElement
public class CreateCategoryRequest {
    private String name;
    private MultipartFile imageFile;
    private Boolean isActive = true;
}
