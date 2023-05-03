package hcmute.puzzle.infrastructure.models.payload.response;

import hcmute.puzzle.infrastructure.dtos.olds.CandidateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatesAndApplicationResultsResponse {
    CandidateDto candidateDTO;
    boolean result = false;
}
