package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.JobPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface JobPostRepository extends JpaRepository<JobPostEntity, Long> {

  @Query(
      "SELECT c FROM CandidateEntity c WHERE c.id in (SELECT a.candidateEntity.id FROM ApplicationEntity a WHERE a.jobPostEntity.id = :jobPostId)")
  Set<CandidateEntity> getCandidateApplyJobPost(@Param("jobPostId") long jobPostId);

  @Query(
      "SELECT jp FROM JobPostEntity jp WHERE jp.id in (SELECT a.jobPostEntity.id FROM ApplicationEntity a WHERE a.candidateEntity.id = :candidateId)")
  Set<JobPostEntity> getJobPostCandidateApplied(@Param("candidateId") long candidateId);

  @Query(
      "SELECT jp FROM JobPostEntity jp, ApplicationEntity ap, CandidateEntity can WHERE can.id = :candidateId AND ap.candidateEntity.id = can.id AND ap.jobPostEntity.id = jp.id")
  Set<JobPostEntity> findAllByAppliedCandidateId(@Param("candidateId") long candidateId);

  @Query("SELECT jp FROM JobPostEntity jp WHERE jp.createdEmployer.id = :employerId")
  Set<JobPostEntity> findAllByCreatedEmployerId(@Param("employerId") long employerId);

  @Query("SELECT jp FROM JobPostEntity jp ORDER BY jp.dueTime DESC NULLS LAST ")
  Set<JobPostEntity> getJobPostDueSoon();

  @Query("SELECT jp FROM JobPostEntity jp ORDER BY jp.createTime ASC NULLS LAST")
  Set<JobPostEntity> getHotJobPost();

  @Query("SELECT jp FROM JobPostEntity jp WHERE jp.isActive = TRUE")
  Set<JobPostEntity> findAllByActiveIsTrue();

  @Query("SELECT jp FROM JobPostEntity jp WHERE jp.isActive = FALSE")
  Set<JobPostEntity> findAllByActiveIsFalse();

  // https://stackoverflow.com/questions/13350858/count-the-number-of-rows-in-many-to-many-relationships-in-hibernate
  @Query("SELECT COUNT(jp) FROM JobPostEntity jp WHERE jp.id IN (SELECT jps.id FROM UserEntity u INNER JOIN u.viewJobPosts jps WHERE u.id =:userId)")
  long getViewedJobPostAmountByUser(@Param("userId") long userId);

  @Query("SELECT COUNT(usr) FROM UserEntity usr WHERE usr.id IN (SELECT u.id FROM JobPostEntity jp INNER JOIN jp.viewedUsers u WHERE jp.id =:jobPostId AND u.candidateEntity.id IS NOT NULL)")
  long getViewedCandidateAmountByJobPostId(@Param("jobPostId") long jobPostId);

  @Query("SELECT COUNT(usr) FROM UserEntity usr WHERE usr.id IN (SELECT u.id FROM JobPostEntity jp INNER JOIN jp.viewedUsers u WHERE jp.createdEmployer.id =:employerId AND u.candidateEntity.id IS NOT NULL)")
  long getViewedCandidateAmountToJobPostCreatedByEmployer(@Param("employerId") long employerId);

  @Query(
      "SELECT jp FROM JobPostEntity jp WHERE jp.isActive = :isActive AND jp.createdEmployer.id =:employerId")
  Set<JobPostEntity> findAllByActiveAndCreatedEmployerId(
      @Param("isActive") boolean isActive, @Param("employerId") long employerId);

  @Query(
      "SELECT jp.id FROM JobPostEntity jp WHERE jp.isActive = :isActive AND jp.createTime >= :createTime")
  List<Long> getJobPostIdByActiveAndLessThenCreatedTime(
      @Param("isActive") boolean isActive, @Param("createTime") Date createTime);

  //  @Query(
  //          "SELECT jp FROM JobPostEntity jp, CandidateEntity can WHERE can.id = :candidateId AND
  // jp.id in can.savedJobPost")
  //  Set<JobPostEntity> findAllBySavedCandidateId(@Param("candidateId") long candidateId);
}
