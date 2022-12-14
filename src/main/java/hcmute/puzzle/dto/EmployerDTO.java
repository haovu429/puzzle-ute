package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerDTO {
  private long id;
  private String firstname;
  private String lastname;
  private String recruitmentEmail;
  private String recruitmentPhone;

  //  private List<Long> followCandidateIds = new ArrayList<>();
  //  private List<Long> jobPostIds = new ArrayList<>();
  //  private List<Long> evaluateIds = new ArrayList<>();
  private long userId;
}
