package hcmute.puzzle.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_post_view")
public class JobPostView extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;


	@JoinColumn(name = "email", nullable = false)
	private String email;

	@JoinColumn(name = "job_post_id", nullable = false)
	private Long jobPostId;

}
