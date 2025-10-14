package com.taskboard.userservice.infrastructure.repository;

import com.taskboard.userservice.domain.model.UserStatistics;
import com.taskboard.userservice.domain.repository.UserStatisticsRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory stub implementation of UserStatisticsRepository for testing.
 */
public class UserStatisticsRepositoryStub implements UserStatisticsRepository {
    
    private final Map<Long, UserStatistics> statistics = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public UserStatistics save(UserStatistics userStatistics) {
        if (userStatistics.getId() == null) {
            userStatistics.setId(idGenerator.getAndIncrement());
        }
        statistics.put(userStatistics.getId(), userStatistics);
        return userStatistics;
    }
    
    @Override
    public Optional<UserStatistics> findById(Long id) {
        return Optional.ofNullable(statistics.get(id));
    }
    
    @Override
    public Optional<UserStatistics> findByUserId(Long userId) {
        return statistics.values().stream()
            .filter(stat -> stat.getUserId().equals(userId))
            .findFirst();
    }
    
    @Override
    public List<UserStatistics> findAll() {
        return new ArrayList<>(statistics.values());
    }
    
    @Override
    public void delete(UserStatistics userStatistics) {
        statistics.remove(userStatistics.getId());
    }
    
    @Override
    public void deleteById(Long id) {
        statistics.remove(id);
    }
    
    @Override
    public void deleteByUserId(Long userId) {
        statistics.values().removeIf(stat -> stat.getUserId().equals(userId));
    }
    
    @Override
    public boolean existsByUserId(Long userId) {
        return statistics.values().stream()
            .anyMatch(stat -> stat.getUserId().equals(userId));
    }
    
    @Override
    public long count() {
        return statistics.size();
    }
}

