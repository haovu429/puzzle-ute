package hcmute.puzzle.infrastructure.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {
	@NotBlank
	private String fullName;
	@Email
	@NotBlank
	private String email;
	private String phone;
	private String coverLetter;
	private String cvUrl;
}
