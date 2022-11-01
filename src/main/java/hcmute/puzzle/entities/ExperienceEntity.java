package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "experience")
public class ExperienceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "title", columnDefinition = "VARCHAR(50)")
  private String title;

  @Column(name = "employment_type", columnDefinition = "VARCHAR(50)")
  private String employmentType;

  @Column(name = "company", columnDefinition = "VARCHAR(100)")
  private String company;

  @Column(name = "is_working")
  private boolean isWorking = false;

  @Column(name = "industry", columnDefinition = "VARCHAR(200)")
  private String industry;

  @Column(name = "start_date")
  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Column(name = "end_date")
  @Temporal(TemporalType.DATE)
  private Date endDate;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "skills", columnDefinition = "TEXT")
  private String skills;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private CandidateEntity candidateEntity;

//  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//  @JoinTable(
//      name = "experience_have_skill",
//      joinColumns = @JoinColumn(name = "experience_id"),
//      inverseJoinColumns = @JoinColumn(name = "skill_id"))
//  private Set<SkillEntity> skillEntities = new HashSet<>();
}
