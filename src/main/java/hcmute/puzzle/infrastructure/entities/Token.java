package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "token")
public class Token extends Auditable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private long id;

  @Column(name = "token", nullable = false)
  private String token;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expiry_time", nullable = false)
  private Date expiryTime;

  @Column(name = "type", columnDefinition = "VARCHAR(100)", nullable = false)
  private String type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
