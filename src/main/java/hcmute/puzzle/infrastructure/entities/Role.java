package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role extends Auditable implements Serializable {
  @Id
  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDelete = false;

  @ManyToMany(mappedBy = "roles")
  @Builder.Default
  private Set<User> users = new HashSet<>();

  public Role(String code) {
    this.code = code;
    this.setName(code.toLowerCase());
  }

  //  @ManyToMany(
  //      mappedBy = "roles",
  //      cascade = CascadeType.ALL) // mappeBy = List được định nghĩa bên UserEntity
  //  private List<AccountEntity> users = new ArrayList<>();
}
