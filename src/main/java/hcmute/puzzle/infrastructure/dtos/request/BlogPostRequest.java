package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class BlogPostRequest {

	@NotBlank
	@Size(min = 20)
	private String title;

	@NotBlank
	@Size(min = 50)
	private String body;

	private MultipartFile thumbnail;

	@NotNull
	private long categoryId;

	private String tags;
}
