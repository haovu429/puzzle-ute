package hcmute.puzzle.infrastructure.dtos.news;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubCommentRequest {
	private String nickname;
	private String email;
	@NotBlank
	private String content;
}
