package hcmute.puzzle.entities;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "candidate")
public class CandidateEntity implements Serializable {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(50)")
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(50)")
  private String lastName;

  @Column(name = "email_contact", nullable = false, columnDefinition = "VARCHAR(100)")
  private String emailContact;

  @Column(name = "phone_num", columnDefinition = "VARCHAR(20)")
  private String phoneNum;

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

  @Column(name = "skills", columnDefinition = "TEXT")
  private String skills;

  @Column(name = "services", columnDefinition = "TEXT")
  private String services;

  @Column(name = "position", columnDefinition = "TEXT")
  private String position;

  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinTable(
      name = "follow_employer",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "employer_id"))
  private Set<EmployerEntity> followingEmployers = new HashSet<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ApplicationEntity> applicationEntities = new HashSet<>();

  //  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  //  @JoinTable(
  //      name = "have_skill",
  //      joinColumns = @JoinColumn(name = "candidate_id"),
  //      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  //  private Set<SkillEntity> skillEntities = new HashSet<>();

  //  @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  //  @JoinTable(
  //      name = "have_service",
  //      joinColumns = @JoinColumn(name = "candidate_id"),
  //      inverseJoinColumns = @JoinColumn(name = "service_id"))
  //  private Set<ServiceEntity> serviceEntities = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "following_company",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "company_id"))
  private Set<CompanyEntity> followingCompany = new HashSet<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ExperienceEntity> experienceEntities = new HashSet<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<EvaluateEntity> evaluateEntities = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "saved_job_post",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "job_post_id"))
  private Set<JobPostEntity> savedJobPost = new HashSet<>();

  @OneToMany(mappedBy = "candidateEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<JobAlertEntity> jobAlertEntities = new HashSet<>();

  @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity userEntity;

  @Override
  public String toString() {
    return "CandidateEntity{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", emailContact='" + emailContact + '\'' +
            ", phoneNum='" + phoneNum + '\'' +
            '}';
  }
}
