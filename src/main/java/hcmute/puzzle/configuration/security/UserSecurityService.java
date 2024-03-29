package hcmute.puzzle.configuration.security;

import freemarker.template.TemplateException;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;

import hcmute.puzzle.services.impl.RoleService;
import hcmute.puzzle.services.impl.SecurityService;
import hcmute.puzzle.utils.Provider;
import hcmute.puzzle.utils.RedisUtils;
import hcmute.puzzle.utils.login_google.GooglePojo;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserSecurityService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  JwtTokenProvider tokenProvider;

  @Autowired
  RedisUtils redisUtils;

  @Autowired
  AuthenticationManager authenticationManager;

  @PersistenceContext
  public EntityManager em;

  @Autowired
  RoleService roleService;

  @Autowired
  SecurityService securityService;

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Avoid ErrorDefine: failed to lazily initialize a collection of role:
    // hcmute.puzzle.entities.UserEntity.roles, could not initialize proxy - no Session
    // https://www.baeldung.com/hibernate-initialize-proxy-exception

    User user = userRepository.getUserByEmailJoinFetch(email);
    // UserEntity userEntity = userRepository.getUserByEmailJoinFetch(email);

    //        EntityGraph graph = this.em.getEntityGraph("graph.User.roles");
    //        Map hints = new HashMap();
    //        hints.put("javax.persistence.fetchgraph", graph);
    //        UserEntity user = this.em.(UserEntity.class, userId, hints);

    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(user);
  }

  public  Map<String, Object> processOAuthPostLogin(GooglePojo googlePojo) {
    User existUser = userRepository.getUserByEmail(googlePojo.getEmail());

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

  public User createNewAccountAfterOAuthGoogleLoginSuccess(GooglePojo googlePojo) {
    User newUser = User.builder()
                       .email(googlePojo.getEmail())
                       .fullName(googlePojo.getName())
                       .avatar(googlePojo.getPicture())
                       .emailVerified(googlePojo.isVerified_email())
                       .locale(googlePojo.getLocale())
                       .provider(Provider.GOOGLE)
                       .isActive(true)
                       .isDeleted(false)
                       .build();
    //    newUser.setEmail(googlePojo.getEmail());
    //    newUser.setFullName(googlePojo.getName());
    //    newUser.setFullName(googlePojo.getName());
    //    newUser.setAvatar(googlePojo.getPicture());
    //    newUser.setEmailVerified(googlePojo.isVerified_email());
    //    newUser.setLocale(googlePojo.getLocale());
    //    newUser.setProvider(Provider.GOOGLE);
    List<String> roleCodes = new ArrayList<>();
    roleCodes.add("user");
//    Role role = roleRepository.findById("user").orElseThrow(() -> new NotFoundDataException("Not found role"));
//    Set<Role> roles = new HashSet<>();
//    roles.add(role);
//    newUser.setRoles(roles);

    List<Role> rolesFromDb = roleRepository.findAllByCodeIn(roleCodes);
    if (rolesFromDb == null || rolesFromDb.isEmpty()) {
      throw new NotFoundException("NOT_FOUND_ROLE");
    }
    roleService.setRoleWithCreateAccountTypeUser(rolesFromDb.stream().map(Role::getCode).collect(Collectors.toList()), newUser);
    // Save to DB
    userRepository.save(newUser);
    try {
      securityService.sendTokenVerifyAccount(newUser.getEmail());
    } catch (MessagingException | ExecutionException | IOException | TemplateException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return userRepository.save(newUser);
  }

  public void updateCustomerAfterOAuthLoginSuccess(User user, GooglePojo googlePojo) {
    if (user.getFullName() == null) {
      user.setFullName(googlePojo.getName());
    }

    if (user.getAvatar() == null){
      user.setAvatar(googlePojo.getPicture());
    }
    if (user.getProvider() != Provider.GOOGLE) {
      user.setProvider(Provider.GOOGLE);
    }

    if (!user.getEmailVerified()) {
      user.setEmailVerified(true);
    }
    userRepository.save(user);
  }

  public String generateTokenOAuth2(String email) {
    try {
      User user = userRepository.getUserByEmailJoinFetch(email);
      CustomUserDetails customUserDetails = new CustomUserDetails(user);
      if (user == null) {
        return null;
      }
      //Collection<? extends GrantedAuthority> a = customUserDetails.getAuthorities();
      //      Authentication authentication =userRepository.getUserByEmailJoinFetch(email)
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
