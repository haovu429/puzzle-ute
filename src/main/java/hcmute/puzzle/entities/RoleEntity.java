package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class RoleEntity implements Serializable {
  @Id
  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  public RoleEntity(String code) {
    this.code = code;
    this.setName(code.toLowerCase());
  }

  //  @ManyToMany(
  //      mappedBy = "roles",
  //      cascade = CascadeType.ALL) // mappeBy = List được định nghĩa bên UserEntity
  //  private List<AccountEntity> users = new ArrayList<>();
}
