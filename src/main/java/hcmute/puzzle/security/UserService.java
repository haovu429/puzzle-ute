package hcmute.puzzle.security;

import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.login_google.GooglePojo;
import hcmute.puzzle.repository.RoleRepository;
import hcmute.puzzle.repository.UserRepository;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired JwtTokenProvider tokenProvider;

  @Autowired RedisUtils redisUtils;

  @Autowired AuthenticationManager authenticationManager;

  // @PersistenceContext public EntityManager em;

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Avoid Error: failed to lazily initialize a collection of role:
    // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
    // https://www.baeldung.com/hibernate-initialize-proxy-exception
    UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);
    // UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);

    if (userEntity == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(userEntity);
  }

  public String processOAuthPostLogin(GooglePojo googlePojo) {
    UserEntity existUser = userRepository.getUserByEmail(googlePojo.getEmail());

    if (existUser == null) {
      existUser = createNewAccountAfterOAuthGoogleLoginSuccess(googlePojo);
    } else {
      updateCustomerAfterOAuthLoginSuccess(existUser, googlePojo);
    }
    if (existUser == null) {
      throw new CustomException("Error create account from OAuth2");
    }

    return generateTokenOAuth2(existUser.getEmail());
  }

  public UserEntity createNewAccountAfterOAuthGoogleLoginSuccess(GooglePojo googlePojo) {
    UserEntity newUser = new UserEntity();
    newUser.setEmail(googlePojo.getEmail());
    newUser.setFullName(googlePojo.getName());
    newUser.setAvatar(googlePojo.getPicture());
    newUser.setEmailVerified(googlePojo.isVerified_email());
    newUser.setLocale(googlePojo.getLocale());
    newUser.setProvider(Provider.GOOGLE);
    Optional<RoleEntity> role = roleRepository.findById("user");
    if (role.isEmpty()) {
      throw new CustomException("role user isn't exist");
    }
    newUser.getRoles().add(role.get());
    newUser.setActive(true);

    return userRepository.save(newUser);
  }

  public void updateCustomerAfterOAuthLoginSuccess(UserEntity userEntity, GooglePojo googlePojo) {
    if (userEntity.getFullName() == null) {
      userEntity.setFullName(googlePojo.getName());
    }

    if (userEntity.getAvatar() == null)
    userEntity.setProvider(Provider.GOOGLE);
    userRepository.save(userEntity);
  }

  public String generateTokenOAuth2(String email) {
    try {
      UserEntity user = userRepository.getUserByEmailJoinFetch(email);
      CustomUserDetails customUserDetails = new CustomUserDetails(user);
      if (user == null) {
        return null;
      }
      //Collection<? extends GrantedAuthority> a = customUserDetails.getAuthorities();
//      Authentication authentication =
//          authenticationManager.authenticate(
//              new UsernamePasswordAuthenticationToken(
//                  email, null, customUserDetails.getAuthorities()));
//      //        Set in security context
//      SecurityContextHolder.getContext().setAuthentication(authentication);
//      Authentication authentication1 = authenticationManager.authenticate( createNewAccountAfterOAuthLoginSuccess()
//              authenticationManager.authenticate(
      Long JWT_EXPIRATION = (long) (60 * 60 * 1000); // 1 h vi token google 1 h se het han

      // get jwt token
      String jwt =
          tokenProvider.generateToken(
                  customUserDetails, JWT_EXPIRATION);

      // store token in redis
      redisUtils.set(email, jwt);
      return jwt;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Authentication error");
    }
  }
}
