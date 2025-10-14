package com.taskboard.userservice.infrastructure.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Stub implementation of UserRepository for testing purposes.
 * Provides in-memory storage for users.
 */
public class UserRepositoryStub implements UserRepository {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1L;
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
            .filter(user -> Objects.equals(user.getUsername(), username))
            .findFirst();
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
            .filter(user -> Objects.equals(user.getEmail(), email))
            .findFirst();
    }
    
    @Override
    public List<User> findByRole(UserRole role) {
        return users.values().stream()
            .filter(user -> user.getRole() == role)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByStatus(UserStatus status) {
        return users.values().stream()
            .filter(user -> user.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> allUsers = new ArrayList<>(users.values());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allUsers.size());
        List<User> pageContent = allUsers.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allUsers.size());
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user = user.toBuilder().id(nextId++).build();
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
            .anyMatch(user -> Objects.equals(user.getUsername(), username));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
            .anyMatch(user -> Objects.equals(user.getEmail(), email));
    }
    
    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return users.values().stream()
            .anyMatch(user -> Objects.equals(user.getEmail(), email) && !Objects.equals(user.getId(), id));
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public List<User> findByCreatedAtGreaterThan(LocalDateTime dateTime) {
        return users.values().stream()
            .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(dateTime))
            .collect(Collectors.toList());
    }
    
    public void clear() {
        users.clear();
        nextId = 1L;
    }
}
