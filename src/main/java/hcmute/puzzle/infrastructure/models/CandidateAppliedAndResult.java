package hcmute.puzzle.infrastructure.models;

import hcmute.puzzle.infrastructure.dtos.olds.ApplicationDto;
import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateAppliedAndResult {
	String position;
	CandidateDto candidate;
	ApplicationDto application;
}
