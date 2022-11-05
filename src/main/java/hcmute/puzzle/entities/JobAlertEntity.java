package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_alert")
public class JobAlertEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "tag", columnDefinition = "TEXT")
  private String tag;

  @Column(name = "industry", columnDefinition = "VARCHAR(200)")
  private String industry;

  @Column(name = "employment_type", columnDefinition = "VARCHAR(100)")
  private String employmentType;

  @Column(name = "workplace_type", columnDefinition = "VARCHAR(100)")
  private String workplaceType;

  @Column(name = "city", columnDefinition = "VARCHAR(50)")
  private String city;

  @Column(name = "min_budget")
  private long minBudget = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private CandidateEntity candidateEntity;
}
