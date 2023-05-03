package hcmute.puzzle.infrastructure.dtos.olds;

import com.sun.istack.NotNull;
import hcmute.puzzle.infrastructure.entities.Auditable;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RoleDto {

  @NotNull private String code;

  @NotBlank(message = "Don't blank")
  @Size(min = 1, max = 50, message = "Size must be between 1 and 50")
  private String name;
}
