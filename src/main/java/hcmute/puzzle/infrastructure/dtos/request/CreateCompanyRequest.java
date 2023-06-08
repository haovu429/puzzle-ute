package hcmute.puzzle.infrastructure.dtos.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {
	private long id;
	@NotEmpty
	private String name;
	private String description;
	private String image;
	private String website;
	private Long createdEmployerId;
}
