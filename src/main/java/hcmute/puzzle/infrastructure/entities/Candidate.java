package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "candidate")
public class Candidate extends Auditable implements Serializable {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(100)")
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
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
  private Boolean blind;

  @Column(name = "deaf")
  private Boolean deaf;

  @Column(name = "communication_dis")
  private Boolean communicationDis;

  @Column(name = "hand_dis")
  private Boolean handDis;

  @Column(name = "labor")
  private Boolean labor;

  @Column(name = "detail_dis", columnDefinition = "TEXT")
  private String detailDis;

  @Column(name = "verified_dis")
  private Boolean verifiedDis;

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
  @Builder.Default
  private Set<Employer> followingEmployers = new HashSet<>();

  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Application> applicationEntities = new ArrayList<>();

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

  @Builder.Default
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "following_company",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "company_id"))
  private Set<Company> followingCompany = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Experience> experienceEntities = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Evaluate> evaluateEntities = new ArrayList<>();

  @Builder.Default
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "saved_job_post",
      joinColumns = @JoinColumn(name = "candidate_id"),
      inverseJoinColumns = @JoinColumn(name = "job_post_id"))
  private Set<JobPost> savedJobPost = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<JobAlert> jobAlertEntities = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

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
