package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "application")
public class ApplicationEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "result", columnDefinition = "VARCHAR(50)")
  private String result;

  @Column(name = "note", columnDefinition = "VARCHAR(200)")
  private String note;

  @Column(name = "create_time")
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;


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
