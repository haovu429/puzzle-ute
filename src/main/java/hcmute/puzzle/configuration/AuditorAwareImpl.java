package hcmute.puzzle.configuration;

import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.utils.Constant;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

  Logger logger = LogManager.getLogger(AuditorAwareImpl.class);

  @Override
  public Optional<String> getCurrentAuditor() {
    User currentUser = null;
    try {
      if (SecurityContextHolder.getContext().getAuthentication() != null
              && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
        currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    // String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    String auditor = currentUser != null ? currentUser.getEmail() : Constant.SYSTEM_MAIL;
    return Optional.of(auditor);
  }
}
