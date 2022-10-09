package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_post")
public class JobPostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "title", columnDefinition = "VARCHAR(200)")
  private String title;

  @Column(name = "employment_type", columnDefinition = "VARCHAR(50)")
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

  @Column(name = "budget")
  private long budget;

  @Column(name = "due_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dueTime;

  @Column(name = "work_status", columnDefinition = "VARCHAR(20)")
  private String workStatus;

  @Column(name = "blind")
  private boolean blind;

  @Column(name = "deaf")
  private boolean deaf;

  @Column(name = "communication_dis")
  private boolean communicationDis;

  @Column(name = "hand_dis")
  private boolean handDis;

  @Column(name = "labor")
  private boolean labor;

  @OneToMany(mappedBy = "jobPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ApplicationEntity> applicationEntities = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_employer", nullable = false)
  private EmployerEntity createdEmployer;

  @ManyToMany(mappedBy = "savedJobPost", cascade = CascadeType.DETACH)
  private Set<CandidateEntity> savedCandidates = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "needed_skill",
      joinColumns = @JoinColumn(name = "job_post_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private Set<SkillEntity> skillEntities = new HashSet<>();

  @OneToMany(mappedBy = "jobPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<CompanyEntity> companyEntities = new HashSet<>();
}
