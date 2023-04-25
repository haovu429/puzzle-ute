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
@Table(name = "extra_info")
public class ExtraInfoEntity extends Auditable implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", columnDefinition = "VARCHAR(200)")
  private String name;

  @Column(name = "type", columnDefinition = "VARCHAR(100)")
  private String type;

  @Column(name = "is_active")
  private boolean isActive;
}
