package com.taskboard.user.controller;

import com.taskboard.user.dto.UserDto;
import com.taskboard.user.dto.UserProfileDto;
import com.taskboard.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** REST controller for administrative user operations. Requires ADMIN role for all operations. */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

  private final UserService userService;

  /** Get all users (paginated) */
  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    log.info("Admin request to get all users: page={}, size={}", page, size);
    List<UserDto> users = userService.getAllUsers(page, size);
    return ResponseEntity.ok(users);
  }

  /** Get user by ID */
  @GetMapping("/{userId}")
  public ResponseEntity<UserProfileDto> getUserById(@PathVariable String userId) {
    log.info("Admin request to get user by ID: {}", userId);
    UserProfileDto user = userService.getUserById(userId);
    return ResponseEntity.ok(user);
  }

  /** Create new user */
  @PostMapping
  public ResponseEntity<UserProfileDto> createUser(@Valid @RequestBody UserDto userDto) {
    log.info("Admin request to create user: {}", userDto.getEmail());
    UserProfileDto createdUser = userService.createUser(userDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /** Update user */
  @PutMapping("/{userId}")
  public ResponseEntity<UserProfileDto> updateUser(
      @PathVariable String userId, @Valid @RequestBody UserDto userDto) {
    log.info("Admin request to update user: {}", userId);
    UserProfileDto updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  /** Delete user */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
    log.info("Admin request to delete user: {}", userId);
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  /** Block/unblock user */
  @PutMapping("/{userId}/block")
  public ResponseEntity<UserProfileDto> toggleUserBlock(@PathVariable String userId) {
    log.info("Admin request to toggle user block: {}", userId);
    UserProfileDto user = userService.toggleUserBlock(userId);
    return ResponseEntity.ok(user);
  }

  /** Update user role */
  @PutMapping("/{userId}/role")
  public ResponseEntity<UserProfileDto> updateUserRole(
      @PathVariable String userId, @RequestParam String role) {
    log.info("Admin request to update user role: userId={}, role={}", userId, role);
    UserProfileDto user = userService.updateUserRole(userId, role);
    return ResponseEntity.ok(user);
  }

  /** Search users by email or name */
  @GetMapping("/search")
  public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
    log.info("Admin request to search users: query={}", query);
    List<UserDto> users = userService.searchUsers(query);
    return ResponseEntity.ok(users);
  }
}
