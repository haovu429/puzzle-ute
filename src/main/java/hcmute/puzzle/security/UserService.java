package hcmute.puzzle.security;

import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  @Autowired private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.getByEmail(email);
    if (userEntity == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(userEntity);
  }
}
