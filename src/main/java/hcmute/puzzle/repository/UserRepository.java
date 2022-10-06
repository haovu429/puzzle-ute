package hcmute.puzzle.repository;

import hcmute.puzzle.entities.UserEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Scope(value = "singleton")
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT a FROM UserEntity a WHERE a.email = ?1")
  UserEntity getUserByEmail(String email);

  @Query("SELECT a FROM UserEntity a WHERE a.email = ?1 AND a.password = ?2")
  UserEntity getUserByAccount(String email, String password);

  Optional<UserEntity> findByEmail(String email);

  UserEntity getByEmail(String email);
}
