package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorageFileDTO {
  private long id;
  private String type;
  private String name;
  private String url;
  private long codeId;
}
