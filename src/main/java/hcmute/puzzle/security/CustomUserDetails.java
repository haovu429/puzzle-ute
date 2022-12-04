package hcmute.puzzle.security;

import hcmute.puzzle.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
  UserEntity user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Mặc định mình sẽ để tất cả là ROLE_USER. Để demo cho đơn giản.

    return user.getRoles().stream()
        .map(
            roleEntity -> {
              return new SimpleGrantedAuthority(roleEntity.getName());
            })
        .collect(Collectors.toList());
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
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
