package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvaluateDto {
  private long id;
  private int rate;
  private String note;

  private long employerId;
  private long candidateId;
}
