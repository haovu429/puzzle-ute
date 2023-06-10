package hcmute.puzzle.infrastructure.dtos.olds;

import hcmute.puzzle.infrastructure.entities.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StorageFileDto extends Auditable {
  private long id;
  private String type;
  private String name;
  private String url;
  private String cloudinaryPublicId;
  private long objectId;
  private String author;
}
