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
@Table(name = "skill")
public class SkillEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", columnDefinition = "VARCHAR(200)")
  private String name;

  @Column(name = "is_active")
  private boolean isActive;

  @ManyToMany(
          mappedBy = "skillEntities",
          cascade = CascadeType.DETACH)
  private List<CandidateEntity> candidateEntities = new ArrayList<>();

  @ManyToMany(
          mappedBy = "skillEntities",
          cascade = CascadeType.DETACH)
  private List<JobPostEntity> jobPostEntities = new ArrayList<>();

  @ManyToMany(
          mappedBy = "skillEntities",
          cascade = CascadeType.DETACH)
  private List<ExperienceEntity> experienceEntities = new ArrayList<>();
}
