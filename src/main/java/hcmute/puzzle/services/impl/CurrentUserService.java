package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class CurrentUserService {
	User getCurrentUser() {
		User user = null;
		try {
			CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			user = customUserDetails.getUser();
			return user;
		} catch (CustomException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
