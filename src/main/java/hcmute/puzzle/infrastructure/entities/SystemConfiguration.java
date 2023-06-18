package hcmute.puzzle.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "system_configuration")
public class SystemConfiguration extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "key", nullable = false, unique = true, columnDefinition = "TEXT")
	private String key;

	@Column(name = "value", columnDefinition = "TEXT")
	private String value;
}
