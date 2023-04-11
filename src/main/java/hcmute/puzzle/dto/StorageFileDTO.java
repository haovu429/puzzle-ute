package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StorageFileDTO {
  private long id;
  private String type;
  private String name;
  private String url;
  private String cloudinaryPublicId;
  private long objectId;
  private String author;
  private Date createAt;
}
