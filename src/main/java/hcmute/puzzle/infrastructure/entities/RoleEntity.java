package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class RoleEntity extends Auditable implements Serializable {
  @Id
  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;

  public RoleEntity(String code) {
    this.code = code;
    this.setName(code.toLowerCase());
  }

  //  @ManyToMany(
  //      mappedBy = "roles",
  //      cascade = CascadeType.ALL) // mappeBy = List được định nghĩa bên UserEntity
  //  private List<AccountEntity> users = new ArrayList<>();
}
