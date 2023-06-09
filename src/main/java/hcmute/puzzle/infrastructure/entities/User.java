package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.configuration.SystemInfo;
import hcmute.puzzle.utils.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
//@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(name = "graph.User.roles", attributeNodes = @NamedAttributeNode("roles"))
// Avoid ErrorDefine table name is "user" in database
@Table(name = SystemInfo.DatabaseTable.USER)
public class User extends Auditable implements Serializable {

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

  @Column(name = "last_login_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastLoginAt;

  @Column(insertable = false)
  private Boolean isAdmin;

  // https://shareprogramming.net/phan-biet-fetchmode-va-fetchtype-trong-jpa-hibernate/
  // https://viblo.asia/p/van-de-n1-cau-truy-van-trong-hibernate-bWrZn00b5xw
  // @Fetch(FetchMode.JOIN)
  @ManyToMany(fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private Employer employer;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private Candidate candidate;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @Builder.Default
  private List<Document> documentEntities = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @Builder.Default
  private List<Notification> notificationEntities = new ArrayList<>();

  @ManyToMany(mappedBy = "viewedUsers")
  @Builder.Default
  private Set<JobPost> viewJobPosts = new HashSet<>();

  @OneToMany(mappedBy = "regUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Subscription> subscribeEntities = new ArrayList<>();


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Token> tokens = new ArrayList<>();

  public User() {

  }
}

