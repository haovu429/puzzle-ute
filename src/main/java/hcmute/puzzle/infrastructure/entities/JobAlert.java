package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_alert")
public class JobAlertEntity extends Auditable implements Serializable {

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

  @Builder.Default
  @Column(name = "min_budget")
  private long minBudget = 0;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private CandidateEntity candidateEntity;
}
