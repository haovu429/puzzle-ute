package hcmute.puzzle.infrastructure.dtos.response;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class CompanyResponse {
	private Long id;
	@NotEmpty
	private String name;
	private String description;
	private String image;
	private String website;
	private Boolean isPublic;
	private Long createdEmployerId;
}
