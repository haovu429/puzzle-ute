package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDto {
  private long id;
  private String result;
  private String note;
  private long jobPostId;
  private long candidateId;
  private Date createTime;
  private CandidateDto candidateDTO;
}
