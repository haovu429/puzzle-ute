package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

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

  // https://shareprogramming.net/phan-biet-fetchmode-va-fetchtype-trong-jpa-hibernate/
  // https://viblo.asia/p/van-de-n1-cau-truy-van-trong-hibernate-bWrZn00b5xw
  // @Fetch(FetchMode.JOIN)
  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))

  private Set<RoleEntity> roles = new HashSet<>();

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private EmployerEntity employerEntity;

  @OneToOne(mappedBy = "userEntity",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private CandidateEntity candidateEntity;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<DocumentEntity> documentEntities = new HashSet<>();

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<NotificationEntity> notificationEntities = new HashSet<>();
}
