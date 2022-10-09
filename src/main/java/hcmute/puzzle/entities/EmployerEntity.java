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
@Table(name = "employer")
public class EmployerEntity {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "firstname", columnDefinition = "VARCHAR(50)")
  private String firstname;

  @Column(name = "lastname", columnDefinition = "VARCHAR(50)")
  private String lastname;

  @Column(name = "recruitment_email", columnDefinition = "VARCHAR(100)")
  private String recruitmentEmail;

  @Column(name = "recruitment_phone", columnDefinition = "VARCHAR(20)")
  private String recruitmentPhone;

  @ManyToMany(mappedBy = "followingEmployers", cascade = CascadeType.ALL)
  private Set<CandidateEntity> followCandidates = new HashSet<>();

  @OneToMany(mappedBy = "createdEmployer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<JobPostEntity> jobPostEntities = new HashSet<>();

  @OneToMany(mappedBy = "employerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<EvaluateEntity> evaluateEntities = new HashSet<>();

  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity userEntity;
}
