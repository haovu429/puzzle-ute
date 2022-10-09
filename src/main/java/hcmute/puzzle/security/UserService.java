package hcmute.puzzle.security;

import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class UserService implements UserDetailsService {
  @Autowired private UserRepository userRepository;

  // @PersistenceContext public EntityManager em;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Avoid Error: failed to lazily initialize a collection of role:
    // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
    // https://www.baeldung.com/hibernate-initialize-proxy-exception
    UserEntity userEntity = userRepository.getUserByEmail(email);
    // UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);

    //    List result =
    //            em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email  JOIN FETCH
    // u.roles")
    //                    .getResultList();

    if (userEntity == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(userEntity);
  }
}
