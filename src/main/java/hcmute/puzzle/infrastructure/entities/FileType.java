package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder
@Entity
@Table(name = "md_file_type")
public class FileTypeEntity extends Auditable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "category", columnDefinition = "VARCHAR(50)", unique = true, nullable = false)
  private FileCategory category;

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private FileType type;

  @Column(name = "location", columnDefinition = "TEXT")
  @Builder.Default
  private String location = "";

  @Column(name = "storage_name", columnDefinition = "VARCHAR(50)")
  private String storageName;

  @Column(name = "author", columnDefinition = "VARCHAR(100)")
  private String author;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;
}
