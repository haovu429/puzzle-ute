package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CandidateDto {
  private long id;

  @NotBlank
  private String firstName;
  private String lastName;

  @NotBlank
  private String emailContact;
  private String phoneNum;
  private String introduction;
  private String educationLevel;
  private String workStatus;
  private boolean blind;
  private boolean deaf;
  private boolean communicationDis;
  private boolean handDis;
  private boolean labor;
  private String detailDis;
  private boolean verifiedDis;
  private String skills;
  private String services;
  private String position;

  private long userId;
}
