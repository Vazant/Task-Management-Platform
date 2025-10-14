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
            userStatistics = userStatistics.toBuilder().id(idGenerator.getAndIncrement()).build();
        }
        statistics.put(userStatistics.getId(), userStatistics);
        return userStatistics;
    }
    
    @Override
    public Optional<UserStatistics> findByUserId(Long userId) {
        return statistics.values().stream()
            .filter(stat -> stat.getUserId().equals(userId))
            .findFirst();
    }
    
    @Override
    public UserStatistics update(UserStatistics userStatistics) {
        return save(userStatistics);
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
    public void delete(Long id) {
        statistics.remove(id);
    }
    
    @Override
    public List<UserStatistics> save(List<UserStatistics> userStatisticsList) {
        List<UserStatistics> saved = new ArrayList<>();
        for (UserStatistics stat : userStatisticsList) {
            saved.add(save(stat));
        }
        return saved;
    }
    
    @Override
    public List<UserStatistics> findByCompletedTasksGreaterThan(int completedTasks) {
        return statistics.values().stream()
            .filter(stat -> stat.getCompletedTasks() > completedTasks)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    @Override
    public List<UserStatistics> findByTodoTasksGreaterThan(int todoTasks) {
        return statistics.values().stream()
            .filter(stat -> stat.getTodoTasks() > todoTasks)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Simplified implementations for increment methods
    @Override
    public UserStatistics incrementTotalTasks(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementTotalTasks);
    }
    
    @Override
    public UserStatistics incrementCompletedTasks(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementCompletedTasks);
    }
    
    @Override
    public UserStatistics incrementInProgressTasks(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementInProgressTasks);
    }
    
    @Override
    public UserStatistics incrementTodoTasks(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementTodoTasks);
    }
    
    @Override
    public UserStatistics moveTaskFromInProgressToCompleted(Long userId) {
        return updateStatistics(userId, stat -> {
            stat.incrementCompletedTasks();
            // Note: Should decrement in-progress, but we don't have that method
        });
    }
    
    @Override
    public UserStatistics moveTaskFromTodoToInProgress(Long userId) {
        return updateStatistics(userId, stat -> {
            stat.incrementInProgressTasks();
            // Note: Should decrement todo, but we don't have that method
        });
    }
    
    @Override
    public UserStatistics incrementActiveProjects(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementActiveProjects);
    }
    
    @Override
    public UserStatistics incrementTotalProjects(Long userId) {
        return updateStatistics(userId, UserStatistics::incrementTotalProjects);
    }
    
    @Override
    public UserStatistics updateLastActivity(Long userId) {
        return updateStatistics(userId, UserStatistics::updateLastActivity);
    }
    
    @Override
    public UserStatistics updateLastTaskCreated(Long userId) {
        return updateStatistics(userId, UserStatistics::updateLastTaskCreated);
    }
    
    @Override
    public UserStatistics updateLastTaskCompleted(Long userId) {
        return updateStatistics(userId, UserStatistics::updateLastTaskCompleted);
    }
    
    private UserStatistics updateStatistics(Long userId, java.util.function.Consumer<UserStatistics> updater) {
        return findByUserId(userId)
            .map(stat -> {
                UserStatistics updated = stat.toBuilder().build();
                updater.accept(updated);
                return save(updated);
            })
            .orElse(null);
    }
    
    public void clear() {
        statistics.clear();
        idGenerator.set(1);
    }
}