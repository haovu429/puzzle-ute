package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "extra_info")
public class ExtraInfo extends Auditable implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", columnDefinition = "VARCHAR(200)", nullable = false)
  private String name;

  @Column(name = "type", columnDefinition = "VARCHAR(100)", nullable = false)
  private ExtraInfoType type;

  @Column(name = "is_active")
  private Boolean isActive;
}
