package hcmute.puzzle.infrastructure.dtos.olds;

import jakarta.persistence.Column;
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
  private String coverLetter;
  private String fullName;
  private String email;
  private String phone;
  private String cvName;
  private String cv;
  private Long jobPostId;
  private Long candidateId;
  private Date createdAt;
  private Date updatedAt;
  private CandidateDto candidateDTO;
}
