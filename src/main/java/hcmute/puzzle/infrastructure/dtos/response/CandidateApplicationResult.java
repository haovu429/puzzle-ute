package hcmute.puzzle.infrastructure.dtos.response;

import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateApplicationResult {
	private String position;
	private String avatar;
	private CandidateDto candidate;
	private ApplicationDto application;
}
