package hcmute.puzzle.repository;

import hcmute.puzzle.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

// @Scope(value = "singleton")
// @Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


  @Query("SELECT a FROM UserEntity a WHERE a.email = ?1")
  UserEntity getUserByEmail(String email);

  // Avoid ErrorDefine: failed to lazily initialize a collection of role:
  // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
  // https://www.baeldung.com/hibernate-initialize-proxy-exception
  @Query("SELECT DISTINCT a FROM UserEntity a JOIN FETCH a.roles WHERE a.email = ?1")
  UserEntity getUserByEmailJoinFetch(String email);

  @Query("SELECT a FROM UserEntity a WHERE a.email = ?1 AND a.password = ?2")
  UserEntity getUserByAccount(String email, String password);

  Optional<UserEntity> findByEmail(String email);

  @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
  UserEntity getUserByUsername(@Param("username") String username);

  @Query(
      "SELECT COUNT(u) FROM UserEntity u WHERE u.joinDate > :startTime AND u.joinDate <= :endTime")
  long getCountUserJoinInTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

  UserEntity getByEmail(String email);
}
