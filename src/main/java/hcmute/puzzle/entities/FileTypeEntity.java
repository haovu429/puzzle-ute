package hcmute.puzzle.entities;

import hcmute.puzzle.model.enums.FileCategory;
import hcmute.puzzle.model.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "md_file_type")
public class FileTypeEntity {

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

  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  @Column(name = "created_by", columnDefinition = "VARCHAR(100)")
  private String created_by;

  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Date updatedAt;

  @Column(name = "updated_by", columnDefinition = "VARCHAR(100)")
  private String updatedBy;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;
}
