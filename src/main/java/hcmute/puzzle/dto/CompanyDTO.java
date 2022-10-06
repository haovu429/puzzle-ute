package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDTO {
  private long id;
  private String name;
  private String description;
  private String website;
  private boolean isActive;

  private List<Long> followingCandidateIds = new ArrayList<>();
  private List<Long> jobPostIds = new ArrayList<>();
}
