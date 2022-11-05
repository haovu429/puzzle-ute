package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company")
public class CompanyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", columnDefinition = "VARCHAR(100)")
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "website", columnDefinition = "TEXT")
  private String website;

  @Column(name = "is_active")
  private boolean isActive = false;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "created_employer_id")
  private EmployerEntity createdEmployer;

  @ManyToMany(mappedBy = "followingCompany", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<CandidateEntity> followingCandidate = new HashSet<>();

  @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  private Set<JobPostEntity> jobPostEntities = new HashSet<>();

  //  @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  //  private List<NotificationEntity> notificationEntities = new ArrayList<>();
}
