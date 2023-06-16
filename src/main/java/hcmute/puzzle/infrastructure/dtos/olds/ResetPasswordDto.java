package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
  private String oldPass;
  private String newPass;
}
