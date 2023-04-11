package hcmute.puzzle.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file")
public class FileEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private String type;

  @Column(name = "extension", columnDefinition = "VARCHAR(10)")
  private String extension;

  @Column(name = "name", columnDefinition = "VARCHAR(100)")
  private String name;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @Column(name = "codeId")
  private long codeId = -1;

  @Column(name = "author", columnDefinition = "VARCHAR(100)")
  private String author;

}
