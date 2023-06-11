package hcmute.puzzle.infrastructure.dtos.news;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class CreateCommentRequest {
	private String nickname;
	@Email
	private String email;
	@NotBlank
	private String content;
}
