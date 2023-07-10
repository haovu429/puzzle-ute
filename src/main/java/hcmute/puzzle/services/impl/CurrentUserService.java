package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class CurrentUserService {
	public Optional<User> getCurrentUserOptional() {
		User user = null;
		try {
			CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			user = customUserDetails.getUser();
			return Optional.of(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public User getCurrentUser() {
		Optional<User> currentUser =
		this.getCurrentUserOptional();
		if (currentUser.isPresent()) {
			return currentUser.get();
		} else {
			throw new UnauthorizedException("No current user detected");
		}
	}
}
