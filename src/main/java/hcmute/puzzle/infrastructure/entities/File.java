package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder
@Entity
@Table(name = "file")
public class File extends Auditable implements Serializable {

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

  @Column(name = "s3_bucket_key_name")
  private String s3BucketKeyName;

  @Column(name = "provider")
  private String provider;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

}
