package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "candidate")
public class CandidateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(50)")
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(50)")
  private String lastName;

  @Column(name = "introduction", columnDefinition = "TEXT")
  private String introduction;

  @Column(name = "education_level", columnDefinition = "VARCHAR(100)")
  private String educationLevel;

  @Column(name = "work_status", columnDefinition = "VARCHAR(50)")
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

  @Column(name = "detail_dis", columnDefinition = "TEXT")
  private String detailDis;

  @Column(name = "verified_dis")
  private boolean verifiedDis;

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
      name = "follow_employer",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "employer_id"))
  private List<EmployerEntity> followingEmployers = new ArrayList<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ApplicationEntity> applicationEntities = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
      name = "have_skill",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private List<SkillEntity> skillEntities = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
      name = "have_service",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "service_id"))
  private List<ServiceEntity> serviceEntities = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "following_company",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "company_id"))
  private List<CompanyEntity> followingCompany = new ArrayList<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ExperienceEntity> experienceEntities = new ArrayList<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EvaluateEntity> evaluateEntities = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity userEntity;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "saved_job_post",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "company_id"))
  private List<JobPostEntity> savedJobPost = new ArrayList<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<JobAlertEntity> jobAlertEntities = new ArrayList<>();
}