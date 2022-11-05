package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDTO {
  private long id;
  private String name;
  private boolean isActive;
  // private List<Long> candidateIds = new ArrayList<>();
}
