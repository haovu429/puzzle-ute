package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidateDTO {
  private long id;
  private String firstName;
  private String lastName;
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

  //  private List<Long> followingEmployerIds = new ArrayList<>();
  //  private List<Long> applicationIds = new ArrayList<>();
  //  //private List<Long> skillIds = new ArrayList<>();
  //  //private List<Long> serviceIds = new ArrayList<>();
  //  private List<Long> followingCompanyIds = new ArrayList<>();
  //  private List<Long> experienceIds = new ArrayList<>();
  //  private List<Long> evaluateIds = new ArrayList<>();
  //  private List<Long> savedJobPostIds = new ArrayList<>();
  //  private List<Long> jobAlertIds = new ArrayList<>();
  private long userId;
}
