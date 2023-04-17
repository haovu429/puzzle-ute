package hcmute.puzzle.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import hcmute.puzzle.utils.Provider;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
// Avoid ErrorDefine table name is "user" in database
@Table(name = "users")
public class UserEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "username", unique = true, columnDefinition = "VARCHAR(100)")
  private String username;

  @Column(name = "password", columnDefinition = "VARCHAR(100)")
  private String password;

  @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
  private String email;

  @Column(name = "phone", unique = true, columnDefinition = "VARCHAR(20)")
  private String phone;

  @Column(name = "avatar", columnDefinition = "TEXT")
  private String avatar;

  @Column(name = "is_online")
  private boolean isOnline = false;

  @Column(name = "join_date")
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date joinDate;

  @Column(name = "last_online")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastOnline;

  @Column(name = "is_active")
  private boolean isActive = true;

  @Enumerated(EnumType.STRING)
  private Provider provider;

  @Column(name = "full_name", columnDefinition = "VARCHAR(100)")
  private String fullName;

  @Column(name = "email_verified")
  private boolean emailVerified = false;

  @Column(name = "locale", columnDefinition = "VARCHAR(10)")
  private String locale;

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

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private CandidateEntity candidateEntity;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<DocumentEntity> documentEntities = new HashSet<>();

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<NotificationEntity> notificationEntities = new HashSet<>();

  @ManyToMany(mappedBy = "viewedUsers", cascade = CascadeType.DETACH)
  private Set<JobPostEntity> viewJobPosts = new HashSet<>();

  @OneToMany(mappedBy = "packageEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<SubscribeEntity> subscribeEntities = new HashSet<>();

}

