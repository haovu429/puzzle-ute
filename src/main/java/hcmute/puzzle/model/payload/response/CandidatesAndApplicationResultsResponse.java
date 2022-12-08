package hcmute.puzzle.model.payload.response;

import hcmute.puzzle.dto.CandidateDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatesAndApplicationResultsResponse {
    CandidateDTO candidateDTO;
    boolean result = false;
}
