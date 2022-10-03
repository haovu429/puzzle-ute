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
@Table(name = "employer")
public class EmployerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
  private List<CandidateEntity> followCandidates = new ArrayList<>();

  @OneToMany(mappedBy = "createdEmployer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobPostEntity> jobPostEntities;

  @OneToMany(mappedBy = "employerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EvaluateEntity> evaluateEntities;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity userEntity;
}
