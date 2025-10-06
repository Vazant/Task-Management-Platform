package com.taskboard.user.service;

import com.taskboard.user.model.UserEntity;
import com.taskboard.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Custom implementation of UserDetailsService for Spring Security. */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.debug("Loading user by username: {}", username);

    final UserEntity user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> {
                  log.error("User not found with username: {}", username);
                  return new UsernameNotFoundException("User not found: " + username);
                });

    return createSpringSecurityUser(user);
  }

  /**
   * Load user by email.
   *
   * @param email the email
   * @return UserDetails
   * @throws UsernameNotFoundException if user not found
   */
  @Transactional(readOnly = true)
  public UserDetails loadUserByEmail(final String email) throws UsernameNotFoundException {
    log.debug("Loading user by email: {}", email);

    final UserEntity user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.error("User not found with email: {}", email);
                  return new UsernameNotFoundException("User not found: " + email);
                });

    return createSpringSecurityUser(user);
  }

  /**
   * Create Spring Security User from UserEntity.
   *
   * @param user the user entity
   * @return Spring Security User
   */
  private UserDetails createSpringSecurityUser(final UserEntity user) {
    final List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));

    return User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(authorities)
        .accountExpired(!user.isAccountNonExpired())
        .accountLocked(!user.isAccountNonLocked())
        .credentialsExpired(!user.isCredentialsNonExpired())
        .disabled(!user.isEnabled())
        .build();
  }
}
