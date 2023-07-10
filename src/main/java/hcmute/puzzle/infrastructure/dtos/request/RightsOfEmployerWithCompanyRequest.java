package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RightsOfEmployerWithCompanyRequest {
	private Long employerId;
	private Long companyId;
	private String rights;
}
