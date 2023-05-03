package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.TokenEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {
  //    @Query(value = "select u.id, u.avatar, u.email, u.name, u.password" +
  //            " from user as u, token as t" +
  //            " where u.id = t.user_id and t.token = :token", nativeQuery = true )
  @Query("select tk.user from TokenEntity tk where tk.token = :token")
  UserEntity findUserByToken(@Param("token") String token);

  @Query(
      "select tk FROM UserEntity u, TokenEntity tk WHERE u.id = tk.user.id AND u.id = :id AND tk.type=:typeToken")
  List<TokenEntity> findTokensByUserAndType(
      @Param("id") Long id, @Param("typeToken") String typeToken);

  TokenEntity findByToken(String otp);
}
