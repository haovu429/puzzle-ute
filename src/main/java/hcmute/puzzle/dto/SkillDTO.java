package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SkillDTO {
  private long id;
  private String name;
  private boolean isActive;

  private List<Long> candidateIds = new ArrayList<>();
  private List<Long> jobPostIds = new ArrayList<>();
  private List<Long> experienceIds = new ArrayList<>();
}
