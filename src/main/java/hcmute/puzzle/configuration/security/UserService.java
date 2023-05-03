package hcmute.puzzle.configuration.security;

import hcmute.puzzle.infrastructure.entities.RoleEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.utils.login_google.GooglePojo;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    // Avoid ErrorDefine: failed to lazily initialize a collection of role:
    // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
    // https://www.baeldung.com/hibernate-initialize-proxy-exception
    UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);
    // UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);

    if (userEntity == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(userEntity);
  }

  public  Map<String, Object> processOAuthPostLogin(GooglePojo googlePojo) {
    UserEntity existUser = userRepository.getUserByEmail(googlePojo.getEmail());

    if (existUser == null) {
      existUser = createNewAccountAfterOAuthGoogleLoginSuccess(googlePojo);
    } else {
      updateCustomerAfterOAuthLoginSuccess(existUser, googlePojo);
    }
    if (existUser == null) {
      throw new CustomException("ErrorDefine create account from OAuth2");
    }
    Set<String> roles =
            existUser.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());


    Map<String, Object> result = new HashMap<>();
    result.put("jwt", generateTokenOAuth2(existUser.getEmail()));
    result.put("roles", roles);

    return result;
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

    if (userEntity.getAvatar() == null){
      userEntity.setAvatar(googlePojo.getPicture());
    }
    if (userEntity.getProvider() != Provider.GOOGLE) {
      userEntity.setProvider(Provider.GOOGLE);
    }

    if (userEntity.isEmailVerified() != true ) {
      userEntity.setEmailVerified(true);
    }
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
