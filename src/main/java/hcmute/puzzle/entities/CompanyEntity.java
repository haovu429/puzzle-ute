package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
  private boolean isActive;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "created_employer_id")
  private EmployerEntity createdEmployer;

  @ManyToMany(mappedBy = "followingCompany", cascade = CascadeType.DETACH)
  private Set<CandidateEntity> followingCandidate = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_post_id", nullable = false)
  private JobPostEntity jobPostEntity;

  //  @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  //  private List<NotificationEntity> notificationEntities = new ArrayList<>();
}
