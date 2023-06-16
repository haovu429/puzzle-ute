package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExperienceDto {
  private long id;
  private String title;
  private String employmentType;
  private String company;
  private Boolean isWorking;
  private String industry;
  private Date startDate;
  private Date endDate;
  private String description;
  private String skills;
  private Long candidateId;
  // private List<Long> skillIds;
}
