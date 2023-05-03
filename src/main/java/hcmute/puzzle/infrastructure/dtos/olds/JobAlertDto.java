package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobAlertDto {
  private long id;
  private String tag;
  private String industry;
  private String employmentType;
  private String workplaceType;
  private String city;
  private long minBudget;
  private long candidateId;
}
