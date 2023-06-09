package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDto {
  private Long id;
  private String result;
  private String note;
  private Long jobPostId;
  private Long candidateId;
  private Date createdAt;
  private Date updatedAt;
  private CandidateDto candidateDTO;
}
