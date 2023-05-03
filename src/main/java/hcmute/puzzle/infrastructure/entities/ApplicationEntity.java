package hcmute.puzzle.infrastructure.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "application")
public class ApplicationEntity extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "result", columnDefinition = "VARCHAR(50)")
  private String result;

  @Column(name = "note", columnDefinition = "VARCHAR(200)")
  private String note;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "job_post_id", nullable = false)
  private JobPostEntity jobPostEntity;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "candidate_id", nullable = false)
  private CandidateEntity candidateEntity;

  @Override
  public String toString() {
    return "ApplicationEntity{" +
            "id=" + id +
            ", result='" + result + '\'' +
            ", note='" + note + '\'' +
            '}';
  }
}
