package hcmute.puzzle.infrastructure.dtos.olds;

import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExtraInfoDto {
  private Long id;
  private String name;
  private ExtraInfoType type;
  private boolean isActive;
}
