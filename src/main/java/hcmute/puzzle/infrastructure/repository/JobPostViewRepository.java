package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.JobPostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPostViewRepository extends JpaRepository<JobPostView, Long> {
	Optional<JobPostView> findByEmailAndJobPostId(String email, Long jobPostId);

	long countByJobPostId(Long jobPostId);
}
