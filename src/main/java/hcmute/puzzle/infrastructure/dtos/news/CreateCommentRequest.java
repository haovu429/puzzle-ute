package hcmute.puzzle.infrastructure.dtos.news;

import hcmute.puzzle.infrastructure.entities.BlogPostEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
