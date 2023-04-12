package hcmute.puzzle.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDTO {
  private long id;

  @NotNull @NotEmpty private String name;

  @NotNull @NotEmpty private String description;
  private String image;
  private String website;
  private boolean isActive = false;

  private Long createdEmployerId;
  // private List<Long> followingCandidateIds = new ArrayList<>();
  // private List<Long> jobPostIds = new ArrayList<>();
}
