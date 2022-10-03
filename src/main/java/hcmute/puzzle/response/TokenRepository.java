package hcmute.puzzle.response;

import hcmute.puzzle.entities.Token;
import hcmute.puzzle.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TokenRepository extends JpaRepository<Token, Integer> {
  //    @Query(value = "select u.id, u.avatar, u.email, u.name, u.password" +
  //            " from user as u, token as t" +
  //            " where u.id = t.user_id and t.token = :token", nativeQuery = true )
  @Query("select u.user from Token as u where u.token = :token")
  UserEntity findUserByToken(@Param("token") String token);

  @Query(
      value =
          "select o.id, o.created_time, o.token, o.user_id "
              + "from user as u, token as o "
              + "where u.id = o.user_id and u.id = :id",
      nativeQuery = true)
  List<Token> findTokensByUser(@Param("id") Long id);

  Token findByToken(String otp);
}
