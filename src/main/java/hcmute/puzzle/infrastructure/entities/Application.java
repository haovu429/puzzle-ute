package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "application")
public class Application extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "result", columnDefinition = "VARCHAR(50)")
  private String result;

  @Column(name = "note", columnDefinition = "VARCHAR(200)")
  private String note;

  @Column(name = "cover_letter", columnDefinition = "TEXT")
  private String coverLetter;

  @Column(name = "fullName", columnDefinition = "VARCHAR(200)", nullable = false)
  private String fullName;

  @Column(name = "email", columnDefinition = "VARCHAR(200)", nullable = false)
  private String email;

  @Column(name = "phone", columnDefinition = "VARCHAR(20)")
  private String phone;

  @Column(name = "cv_name")
  private String cvName;

  // cv file url
  @Column(name = "cv_file_url", columnDefinition = "TEXT")
  private String cv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_post_id", nullable = false)
  private JobPost jobPost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;

  @Override
  public String toString() {
    return "ApplicationEntity{" +
            "id=" + id +
            ", result='" + result + '\'' +
            ", note='" + note + '\'' +
            '}';
  }
}
