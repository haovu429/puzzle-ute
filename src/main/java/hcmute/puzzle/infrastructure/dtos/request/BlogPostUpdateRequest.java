package hcmute.puzzle.infrastructure.dtos.request;

import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BlogPostUpdateRequest {

	@Size(min = 20)
	private String title;

	@Size(min = 50)
	private String body;

	private MultipartFile thumbnail;

	private Long categoryId;

	private String tags;
}
