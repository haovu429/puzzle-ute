package hcmute.puzzle.infrastructure.entities;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "job_post")
public class JobPost extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "title", columnDefinition = "VARCHAR(200)")
  private String title;

  @Column(name = "position", columnDefinition = "VARCHAR(100)")
  private String position;

  @Column(name = "employment_type", nullable = false, columnDefinition = "VARCHAR(100)")
  private String employmentType;

  @Column(name = "workplace_type", columnDefinition = "VARCHAR(100)")
  private String workplaceType;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "city", columnDefinition = "VARCHAR(100)")
  private String city;

  @Column(name = "address", columnDefinition = "VARCHAR(200)")
  private String address;

  @Column(name = "education_level", columnDefinition = "VARCHAR(100)")
  private String educationLevel;

  @Column(name = "experience_year")
  private int experienceYear;

  @Column(name = "level")
  private String level;

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "min_budget", nullable = false)
  private long minBudget;

  @Column(name = "max_budget", nullable = false)
  private long maxBudget;

//  @Column(name = "create_time")
//  @CreatedDate
//  @Temporal(TemporalType.TIMESTAMP)
//  private Date createTime;

  @Column(name = "deadline")
  @Temporal(TemporalType.TIMESTAMP)
  private Date deadline;

  @Column(name = "expiry_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date expiryDate;

  @Column(name = "work_status", columnDefinition = "VARCHAR(20)")
  private String workStatus;

  @Column(name = "blind")
  @Builder.Default
  private boolean blind = false;

  @Column(name = "deaf")
  @Builder.Default
  private boolean deaf = false;

  @Column(name = "communication_dis")
  @Builder.Default
  private boolean communicationDis = false;

  @Column(name = "hand_dis")
  @Builder.Default
  private boolean handDis = false;

  @Column(name = "labor")
  @Builder.Default
  private boolean labor = false;

  @Column(name = "skills", columnDefinition = "TEXT")
  private String skills;

//  @Column(name = "position", columnDefinition = "TEXT")
//  private String positions;

  @Column(name = "views")
  @Builder.Default
  private Long views = 0L;

  @Column(name = "can_apply")
  private Boolean canApply;

  @Column(name = "is_public")
  private Boolean isPublic;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

//  @Column(name = "subscribe_id")
//  private long subscribeId;

  @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Application> applications = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "viewed_user",
          joinColumns = @JoinColumn(name = "job_post_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private Set<User> viewedUsers = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "created_employer")
  private Employer createdEmployer;

  @ManyToMany(mappedBy = "savedJobPost")
  @Builder.Default
  private Set<Candidate> savedCandidates = new HashSet<>();

  //  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  @JoinTable(
  //      name = "needed_skill",
  //      joinColumns = @JoinColumn(name = "job_post_id"),
  //      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  //  private Set<SkillEntity> skillEntities = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

//  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//  @JoinColumn(name = "subscriber_id")
//  private Subscription subscriber;
}
