package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class RoleEntity extends Auditable implements Serializable {
  @Id
  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;

  @ManyToMany(mappedBy = "roles")
  @Builder.Default
  private Set<UserEntity> users = new HashSet<>();

  public RoleEntity(String code) {
    this.code = code;
    this.setName(code.toLowerCase());
  }

  //  @ManyToMany(
  //      mappedBy = "roles",
  //      cascade = CascadeType.ALL) // mappeBy = List được định nghĩa bên UserEntity
  //  private List<AccountEntity> users = new ArrayList<>();
}
