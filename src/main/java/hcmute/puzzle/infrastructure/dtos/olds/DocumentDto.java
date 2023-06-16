package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentDto {
  private long id;
  private String type;
  private String title;
  private String url;

  private long userId;
}
