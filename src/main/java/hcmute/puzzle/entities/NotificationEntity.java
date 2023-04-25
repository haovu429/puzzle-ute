package hcmute.puzzle.entities;

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
@Table(name = "notification")
public class NotificationEntity extends Auditable implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "type", columnDefinition = "VARCHAR(50)")
  private String type;

  @Column(name = "title", columnDefinition = "VARCHAR(100)")
  private String title;

  @Column(name = "brief", columnDefinition = "VARCHAR(200)")
  private String brief;

  @Column(name = "time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  @Column(name = "is_deleted")
  @Builder.Default
  private boolean isDelete = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private CompanyEntity companyEntity;
}
