package hcmute.puzzle.infrastructure.dtos.news;

import lombok.*;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest implements Serializable {
	private String nickname;
	private String email;
	@NotBlank
	private String content;
}
