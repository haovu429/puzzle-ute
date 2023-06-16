package hcmute.puzzle.configuration.security;

import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
	User user;
	Boolean isAdmin;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			if (role.getCode().equalsIgnoreCase(Roles.ADMIN.getValue())) {
				isAdmin = true;
			}
		}
		if (isAdmin != null && isAdmin.equals(true)) {
			user.setIsAdmin(true);
		}
		// Mặc định sẽ để tất cả là ROLE_USER
		return user.getRoles().stream().map(roleEntity -> {
			return new SimpleGrantedAuthority(roleEntity.getName());
		}).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.getIsActive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getIsActive();
	}
}
