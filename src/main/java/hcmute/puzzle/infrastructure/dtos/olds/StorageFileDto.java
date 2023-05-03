package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StorageFileDto {
  private long id;
  private String type;
  private String name;
  private String url;
  private String cloudinaryPublicId;
  private long objectId;
  private String author;
  private Date createAt;
}
