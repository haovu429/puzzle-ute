package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.utils.Provider;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@EntityListeners(AuditingEntityListener.class)
// Avoid ErrorDefine table name is "user" in database
@Table(name = "users")
public class UserEntity extends Auditable implements Serializable {

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

  @Enumerated(EnumType.STRING)
  private Provider provider;

  @Column(name = "full_name", columnDefinition = "VARCHAR(100)")
  private String fullName;

  @Column(name = "email_verified")
  @Builder.Default
  private boolean emailVerified = false;

  @Column(name = "locale", columnDefinition = "VARCHAR(10)")
  private String locale;

  @Column(name = "is_active")
  @Builder.Default
  private boolean isActive = true;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;

//  @Column(name = "created_at")
//  @Temporal(TemporalType.TIMESTAMP)
//  @CreationTimestamp
//  private Date createdAt;
//
//  @Column(name = "created_by", columnDefinition = "VARCHAR(100)")
//  @CreatedBy
//  private String created_by;
//
//  @Column(name = "updated_at")
//  @Temporal(TemporalType.TIMESTAMP)
//  @UpdateTimestamp
//  private Date updatedAt;
//
//  @Column(name = "updated_by", columnDefinition = "VARCHAR(100)")
//  @LastModifiedBy
//  private String updatedBy;

  @Column(name = "last_login_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastLoginAt;

  // https://shareprogramming.net/phan-biet-fetchmode-va-fetchtype-trong-jpa-hibernate/
  // https://viblo.asia/p/van-de-n1-cau-truy-van-trong-hibernate-bWrZn00b5xw
  // @Fetch(FetchMode.JOIN)
  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Builder.Default
  private List<RoleEntity> roles = new ArrayList<>();

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private EmployerEntity employerEntity;

  @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private CandidateEntity candidateEntity;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<DocumentEntity> documentEntities = new HashSet<>();

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<NotificationEntity> notificationEntities = new HashSet<>();

  @ManyToMany(mappedBy = "viewedUsers", cascade = CascadeType.DETACH)
  @Builder.Default
  private Set<JobPostEntity> viewJobPosts = new HashSet<>();

  @OneToMany(mappedBy = "packageEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<SubscribeEntity> subscribeEntities = new HashSet<>();

  public UserEntity() {

  }
}

