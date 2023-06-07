package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor {

  @Query(
      "SELECT c FROM Candidate c WHERE c.id in (SELECT a.candidate.id FROM Application a WHERE a.jobPost.id = :jobPostId)")
  Set<Candidate> getCandidateApplyJobPost(@Param("jobPostId") long jobPostId);

  @Query(
          "SELECT c FROM Candidate c WHERE c.id in (SELECT a.candidate.id FROM Application a WHERE a.jobPost.id = :jobPostId)")
  Set<Candidate> getCandidateApplyToEmployer(@Param("jobPostId") long jobPostId);

  @Query(
      "SELECT jp FROM JobPost jp WHERE jp.id in (SELECT a.jobPost.id FROM Application a WHERE a.candidate.id = :candidateId)")
  Set<JobPost> getJobPostCandidateApplied(@Param("candidateId") long candidateId);

  @Transactional
  @Modifying
  @Query(" UPDATE JobPost jp SET jp.isDeleted=TRUE WHERE jp.id =:jobPostId")
  void markJobPostWasDelete(@Param("jobPostId") long jobPostId);

  @Query(
      "SELECT jp FROM JobPost jp, Application ap, Candidate can WHERE can.id = :candidateId AND ap.candidate.id = can.id AND ap.jobPost.id = jp.id")
  Set<JobPost> findAllByAppliedCandidateId(@Param("candidateId") long candidateId);

  @Query("SELECT jp FROM JobPost jp WHERE jp.createdEmployer.id = :employerId AND jp.isDeleted=FALSE ")
  Set<JobPost> findAllByCreatedEmployerId(@Param("employerId") long employerId);

  @Query("SELECT jp FROM JobPost jp ORDER BY jp.deadline DESC NULLS LAST ")
  Set<JobPost> getJobPostDueSoon();

  @Query("SELECT jp FROM JobPost jp ORDER BY jp.createTime ASC NULLS LAST")
  Set<JobPost> getHotJobPost();

  @Query("SELECT jp FROM JobPost jp WHERE jp.isActive = TRUE AND jp.isDeleted = FALSE")
  Set<JobPost> findAllByActiveIsTrue();

  @Query("SELECT jp FROM JobPost jp WHERE jp.isActive = FALSE AND jp.isDeleted = FALSE")
  Set<JobPost> findAllByActiveIsFalse();

  // https://stackoverflow.com/questions/13350858/count-the-number-of-rows-in-many-to-many-relationships-in-hibernate
  @Query("SELECT COUNT(jp) FROM JobPost jp WHERE jp.id IN (SELECT jps.id FROM User u INNER JOIN u.viewJobPosts jps WHERE u.id =:userId) AND jp.isDeleted=FALSE")
  long getViewedJobPostAmountByUser(@Param("userId") long userId);

  @Query("SELECT COUNT(usr) FROM User usr WHERE usr.id IN (SELECT u.id FROM JobPost jp INNER JOIN jp.viewedUsers u WHERE jp.id =:jobPostId AND u.candidate.id IS NOT NULL AND jp.isDeleted=FALSE)")
  long getViewedCandidateAmountByJobPostId(@Param("jobPostId") long jobPostId);

  @Query("SELECT COUNT(usr) FROM User usr WHERE usr.id IN (SELECT u.id FROM JobPost jp INNER JOIN jp.viewedUsers u WHERE jp.createdEmployer.id =:employerId AND u.candidate.id IS NOT NULL AND jp.isDeleted=FALSE)")
  long getViewedCandidateAmountToJobPostCreatedByEmployer(@Param("employerId") long employerId);

  @Query(
      "SELECT jp FROM JobPost jp WHERE jp.isActive = :isActive AND jp.createdEmployer.id =:employerId AND jp.isDeleted=FALSE")
  Set<JobPost> findAllByActiveAndCreatedEmployerId(
      @Param("isActive") boolean isActive, @Param("employerId") long employerId);

  @Query(
      "SELECT jp.id FROM JobPost jp WHERE jp.isActive = :isActive AND jp.createTime >= :createTime AND jp.isDeleted=FALSE")
  List<Long> getJobPostIdByActiveAndLessThenCreatedTime(
      @Param("isActive") boolean isActive, @Param("createTime") Date createTime);

  @Query("SELECT SUM(jp.views) FROM JobPost jp WHERE jp.createdEmployer.id =:employerId AND jp.isDeleted=FALSE")
  long getTotalJobPostViewOfEmployer(@Param("employerId") long employerId);

  //  @Query(
  //          "SELECT jp FROM JobPostEntity jp, CandidateEntity can WHERE can.id = :candidateId AND
  // jp.id in can.savedJobPost")
  //  Set<JobPostEntity> findAllBySavedCandidateId(@Param("candidateId") long candidateId);
}
