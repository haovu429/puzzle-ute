package hcmute.puzzle.infrastructure.dtos.news;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubCommentRequest {
	private String nickname;
	private String email;
	@NotBlank
	private String content;
}
