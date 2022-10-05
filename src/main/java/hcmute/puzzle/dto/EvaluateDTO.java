package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvaluateDTO {
    private long id;
    private int rate;
    private String note;

    private long employerId;
    private long candidateId;
}
