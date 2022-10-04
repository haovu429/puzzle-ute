package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class RoleEntity {
  @Id
  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  //  @ManyToMany(
  //      mappedBy = "roles",
  //      cascade = CascadeType.ALL) // mappeBy = List được định nghĩa bên UserEntity
  //  private List<AccountEntity> users = new ArrayList<>();
}
