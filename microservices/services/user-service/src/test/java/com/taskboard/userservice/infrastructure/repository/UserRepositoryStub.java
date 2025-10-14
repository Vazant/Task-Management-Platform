package com.taskboard.userservice.infrastructure.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.domain.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory stub implementation of UserRepository for testing.
 */
public class UserRepositoryStub implements UserRepository {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }
    
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
            .anyMatch(user -> user.getUsername().equals(username));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
            .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return users.values().stream()
            .anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(id));
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public List<User> findByRole(UserRole role) {
        return users.values().stream()
            .filter(user -> user.getRole() == role)
            .toList();
    }
    
    @Override
    public List<User> findByStatus(UserStatus status) {
        return users.values().stream()
            .filter(user -> user.getStatus() == status)
            .toList();
    }
}

