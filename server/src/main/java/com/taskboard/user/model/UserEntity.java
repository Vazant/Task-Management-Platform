package com.taskboard.user.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Entity representing a user in the system. */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private UserRole role = UserRole.USER;

  @Column(length = 255)
  private String avatar;

  @Column(length = 50)
  private String firstName;

  @Column(length = 50)
  private String lastName;

  private LocalDateTime lastLogin;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Embedded private UserPreferences preferences;

  @Column(nullable = false)
  @Builder.Default
  private boolean enabled = true;

  @Column(nullable = false)
  @Builder.Default
  private boolean accountNonExpired = true;

  @Column(nullable = false)
  @Builder.Default
  private boolean accountNonLocked = true;

  @Column(nullable = false)
  @Builder.Default
  private boolean credentialsNonExpired = true;

  @Column(length = 255)
  private String passwordResetToken;

  @Temporal(TemporalType.TIMESTAMP)
  private Date passwordResetTokenExpiry;

  /** Pre-persist method to set default preferences if not provided. */
  @PrePersist
  public void prePersist() {
    if (preferences == null) {
      preferences = new UserPreferences();
    }
    if (role == null) {
      role = UserRole.USER;
    }
  }

  // UserDetails implementation
  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.getAuthority()));
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
