package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

// @Scope(value = "singleton")
// @Repository
public interface UserRepository extends JpaRepository<User, Long> {


  @Query("SELECT a FROM User a WHERE a.email = ?1")
  User getUserByEmail(String email);

  // Avoid ErrorDefine: failed to lazily initialize a collection of role:
  // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
  // https://www.baeldung.com/hibernate-initialize-proxy-exception
  @Query("SELECT DISTINCT a FROM User a LEFT JOIN FETCH a.roles WHERE a.email = ?1 AND a.isDelete = false")
  User getUserByEmailJoinFetch(String email);

  @Query("SELECT a FROM User a WHERE a.email = ?1 AND a.password = ?2")
  User getUserByEmailAndPassword(String email, String password);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.username = :username")
  User getUserByUsername(@Param("username") String username);

  @Query(
      "SELECT COUNT(u) FROM User u WHERE u.createdAt > :startTime AND u.createdAt <= :endTime")
  long getCountUserJoinInTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

  Optional<User> getByEmail(String email);
}
