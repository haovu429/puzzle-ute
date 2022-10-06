package hcmute.puzzle.repository;

import hcmute.puzzle.entities.JobPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPostEntity, Long> {}
