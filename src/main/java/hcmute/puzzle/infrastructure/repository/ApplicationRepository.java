package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Application;
import hcmute.puzzle.infrastructure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // Set<ApplicationEntity> findAllById(Long billId);

    // Optional<ApplicationEntity> findById(Long id);
    @Query(
            "SELECT a FROM Application a WHERE a.candidate.id = :candidateId AND a.jobPost.id = :jobPostId")
    Optional<Application> findApplicationByCanIdAndJobPostId(
            @Param("candidateId") long candidateId, @Param("jobPostId") long jobPostId);

    @Query("SELECT a FROM Application a WHERE a.jobPost.id = :jobPostId")
    Set<Application> findApplicationByJobPostId(@Param("jobPostId") long jobPostId);

    @Query("SELECT a FROM Application a WHERE a.jobPost.createdEmployer.id = :employerId")
    Set<Application> findApplicationByEmployerId(@Param("employerId") long employerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPost.createdEmployer.id = :employerId")
    long getAmountApplicationToEmployer(@Param("employerId") long employerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPost.id = :jobPostId")
    long getAmountApplicationByJobPostId(@Param("jobPostId") long jobPostId);

    @Query("SELECT jp.viewedUsers FROM JobPost jp WHERE jp.id = :jobPostId")
    List<User> findAllUserViewedJobPost(@Param("jobPostId") long jobPostId);
}
