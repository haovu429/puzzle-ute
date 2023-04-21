package hcmute.puzzle.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

  @Column(name = "category", columnDefinition = "VARCHAR(50)")
  private String category;

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private String type;

  @Column(name = "extension", columnDefinition = "VARCHAR(10)")
  private String extension;

  @Column(name = "name", columnDefinition = "VARCHAR(100)")
  private String name;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @Column(name = "location", columnDefinition = "TEXT")
  @Builder.Default
  private String location = "";

  @Column(name = "object_id")
  private long objectId;

  @Column(name = "cloudinary_public_id")
  private String cloudinaryPublicId;

  @Column(name = "created_by", columnDefinition = "VARCHAR(100)")
  private String created_by;

  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Date updatedAt;

  @Column(name = "updated_by", columnDefinition = "VARCHAR(100)")
  private String updatedBy;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDeleted = false;

}
