package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExperienceDTO {
  private long id;
  private String title;
  private String employmentType;
  private String company;
  private boolean isWorking;
  private String industry;
  private Date startDate;
  private Date endDate;
  private String description;
  private String skills;

  private long candidateId;
  //private List<Long> skillIds;
}
