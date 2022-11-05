package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "skill")
public class SkillEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", columnDefinition = "VARCHAR(200)", unique = true)
  private String name;

  @Column(name = "is_active")
  private boolean isActive = false;

  //  @ManyToMany(mappedBy = "skillEntities", cascade = CascadeType.DETACH)
  //  private Set<CandidateEntity> candidateEntities = new HashSet<>();
  //
  //  @ManyToMany(mappedBy = "skillEntities", cascade = CascadeType.DETACH)
  //  private Set<JobPostEntity> jobPostEntities = new HashSet<>();
  //
  //  @ManyToMany(mappedBy = "skillEntities", cascade = CascadeType.DETACH)
  //  private Set<ExperienceEntity> experienceEntities = new HashSet<>();
}
