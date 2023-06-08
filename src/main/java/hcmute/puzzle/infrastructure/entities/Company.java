package hcmute.puzzle.infrastructure.entities;

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
@Entity
@Builder
@Table(name = "company")
public class Company extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "image", columnDefinition = "TEXT")
  private String image;

  @Column(name = "website", columnDefinition = "TEXT")
  private String website;

  @Column(name = "is_public")
  @Builder.Default
  private Boolean isPublic = true;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_employer_id")
  private Employer createdEmployer;

  @Builder.Default
  @ManyToMany(mappedBy = "followingCompany", fetch = FetchType.LAZY)
  private Set<Candidate> followingCandidate = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
  private List<JobPost> jobPostEntities = new ArrayList<>();

  //  @OneToMany(mappedBy = "companyEntity",, fetch = FetchType.LAZY)
  //  private List<NotificationEntity> notificationEntities = new ArrayList<>();

  @PreRemove
  private void preRemove() {
    for (JobPost jobPost : jobPostEntities) {
      jobPost.setCompany(null);
    }
  }
}
