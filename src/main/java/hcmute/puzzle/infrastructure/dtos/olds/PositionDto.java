package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PositionDto {
  private long id;
  private String name;
  private boolean isActive;
}
