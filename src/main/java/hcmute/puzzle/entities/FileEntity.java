package hcmute.puzzle.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
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

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private String type;

  @Column(name = "extension", columnDefinition = "VARCHAR(10)")
  private String extension;

  @Column(name = "name", columnDefinition = "VARCHAR(100)")
  private String name;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @Column(name = "object_id")
  private long objectId;

  @Column(name = "cloudinary_public_id")
  private String cloudinaryPublicId;

  @Column(name = "author", columnDefinition = "VARCHAR(100)")
  private String author;

  @Column(name = "create_at")
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createAt;

}
