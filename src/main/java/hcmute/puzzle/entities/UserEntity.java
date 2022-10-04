package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "username", columnDefinition = "VARCHAR(100)")
  private String username;

  @Column(name = "password", columnDefinition = "VARCHAR(100)")
  private String password;

  @Column(name = "email", columnDefinition = "VARCHAR(100)")
  private String email;

  @Column(name = "phone", columnDefinition = "VARCHAR(20)")
  private String phone;

  @Column(name = "avatar", columnDefinition = "TEXT")
  private String avatar;

  @Column(name = "is_online")
  private boolean isOnline;

  @Column(name = "join_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date joinDate;

  @Column(name = "last_online")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastOnline;

  @Column(name = "is_active")
  private boolean isActive;

  //
  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private List<RoleEntity> roles = new ArrayList<>();

  @OneToOne(mappedBy = "userEntity")
  private EmployerEntity employerEntity;

  @OneToOne(mappedBy = "userEntity")
  private CandidateEntity candidateEntity;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private List<DocumentEntity> documentEntities = new ArrayList<>();

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private List<NotificationEntity> notificationEntities = new ArrayList<>();
}
