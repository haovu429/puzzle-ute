package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "job_post")
public class JobPostEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "title", columnDefinition = "VARCHAR(200)")
  private String title;

  @Column(name = "employment_type", nullable = false, columnDefinition = "VARCHAR(50)")
  private String employmentType;

  @Column(name = "workplace_type", columnDefinition = "VARCHAR(50)")
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

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "min_budget", nullable = false)
  private long minBudget;

  @Column(name = "max_budget", nullable = false)
  private long maxBudget;

  @Column(name = "create_time")
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;

  @Column(name = "due_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dueTime;

  @Column(name = "work_status", columnDefinition = "VARCHAR(20)")
  private String workStatus;

  @Column(name = "blind")
  private boolean blind = false;

  @Column(name = "deaf")
  private boolean deaf = false;

  @Column(name = "communication_dis")
  private boolean communicationDis = false;

  @Column(name = "hand_dis")
  private boolean handDis = false;

  @Column(name = "labor")
  private boolean labor = false;

  @Column(name = "skills", columnDefinition = "TEXT")
  private String skills;

  @Column(name = "positions", columnDefinition = "TEXT")
  private String positions;

  @Column(name = "views")
  private long views = 0;

  @Column(name = "is_active")
  private boolean isActive = false;

  @Column(name = "is_deleted")
  private boolean isDeleted = false;

  @OneToMany(mappedBy = "jobPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ApplicationEntity> applicationEntities = new HashSet<>();

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
          name = "viewed_user",
          joinColumns = @JoinColumn(name = "job_post_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<UserEntity> viewedUsers = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "created_employer")
  private EmployerEntity createdEmployer;

  @ManyToMany(mappedBy = "savedJobPost", cascade = CascadeType.DETACH)
  private Set<CandidateEntity> savedCandidates = new HashSet<>();

  //  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  @JoinTable(
  //      name = "needed_skill",
  //      joinColumns = @JoinColumn(name = "job_post_id"),
  //      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  //  private Set<SkillEntity> skillEntities = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "company_id")
  private CompanyEntity companyEntity;
}
