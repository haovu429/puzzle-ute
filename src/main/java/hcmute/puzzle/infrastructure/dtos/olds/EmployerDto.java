package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerDto {
  private long id;
  private String firstName;
  private String lastName;
  private String recruitmentEmail;
  private String recruitmentPhone;
  private long userId;
  //  private List<Long> followCandidateIds = new ArrayList<>();
  //  private List<Long> jobPostIds = new ArrayList<>();
  //  private List<Long> evaluateIds = new ArrayList<>();
}
