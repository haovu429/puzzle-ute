package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "json_data")
public class JsonData extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "hirize_id")
	private long hirizeId;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "type", columnDefinition = "VARCHAR(200)")
	private JsonDataType type;

	@Column(name = "position", columnDefinition = "TEXT")
	private String data;
}
