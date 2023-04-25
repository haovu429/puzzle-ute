package hcmute.puzzle.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
