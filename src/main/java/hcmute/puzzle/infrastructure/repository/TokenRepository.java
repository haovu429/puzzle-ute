package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.infrastructure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Integer> {
  //    @Query(value = "select u.id, u.avatar, u.email, u.name, u.password" +
  //            " from user as u, token as t" +
  //            " where u.id = t.user_id and t.token = :token", nativeQuery = true )
  @Query("select tk.user from Token tk where tk.token = :token")
  User findUserByToken(@Param("token") String token);

  @Query(
      "select tk FROM User u, Token tk WHERE u.id = tk.user.id AND u.id = :id AND tk.type=:typeToken")
  List<Token> findTokensByUserAndType(
      @Param("id") Long id, @Param("typeToken") String typeToken);

  Token findByToken(String otp);
}
