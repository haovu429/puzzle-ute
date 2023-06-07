package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.configuration.SystemInfo;
import lombok.*;

import javax.persistence.*;
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
@Table(name = "employer")
public class EmployerEntity extends Auditable implements Serializable {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(100)", nullable = false)
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
  private String lastName;

  @Column(name = "recruitment_email", columnDefinition = "VARCHAR(100)")
  private String recruitmentEmail;

  @Column(name = "recruitment_phone", columnDefinition = "VARCHAR(20)")
  private String recruitmentPhone;

  @ManyToMany(mappedBy = "followingEmployers", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<CandidateEntity> followCandidates = new HashSet<>();

  @OneToMany(mappedBy = "createdEmployer", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<JobPostEntity> jobPostEntities = new ArrayList<>();

  @OneToMany(mappedBy = "employerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<EvaluateEntity> evaluateEntities = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity userEntity;

  @OneToMany(mappedBy = "createdEmployer", fetch = FetchType.LAZY)
  @Builder.Default
  private List<CompanyEntity> companyEntities = new ArrayList<>();
}
