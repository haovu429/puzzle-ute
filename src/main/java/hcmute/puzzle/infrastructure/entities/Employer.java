package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employer")
public class Employer extends Auditable implements Serializable {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(100)", nullable = false)
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
  private String lastName;

  @Column(name = "recruitment_email", columnDefinition = "VARCHAR(100)")
  private String recruitmentEmail;

  @Column(name = "recruitment_phone", columnDefinition = "VARCHAR(20)")
  private String recruitmentPhone;

  @ManyToMany(mappedBy = "followingEmployers", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Candidate> followCandidates = new HashSet<>();

  @OneToMany(mappedBy = "createdEmployer", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<JobPost> jobPostEntities = new ArrayList<>();

  @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Evaluate> evaluateEntities = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "createdEmployer", fetch = FetchType.LAZY)
  @Builder.Default
  private List<Company> companyEntities = new ArrayList<>();
}
