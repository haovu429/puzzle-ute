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

  private Date due_time;

  private String work_status;

  private boolean blind;

  @OneToMany(mappedBy = "jobPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ApplicationEntity> applicationEntities = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_employer", nullable = false)
  private EmployerEntity createdEmployer;

  @ManyToMany(mappedBy = "savedJobPost", cascade = CascadeType.DETACH)
  private List<CandidateEntity> candidateEntities = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "needed_skill",
      joinColumns = @JoinColumn(name = "job_post_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private List<SkillEntity> skillEntities = new ArrayList<>();

  @OneToMany(mappedBy = "jobPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CompanyEntity> companyEntities = new ArrayList<>();
}
