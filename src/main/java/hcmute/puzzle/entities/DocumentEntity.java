package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "document")
public class DocumentEntity extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private String type;

  @Column(name = "title", columnDefinition = "VARCHAR(100)")
  private String title;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;
}
