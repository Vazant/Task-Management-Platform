package com.taskboard.userservice.domain.repository;

import java.util.Optional;

import com.taskboard.userservice.domain.model.UserStatistics;

/**
 * Repository interface for UserStatistics domain entity.
 * 
 * <p>This repository provides methods for persisting and retrieving
 * user statistics data, following the repository pattern from domain-driven design.</p>
 * 
 * <p>The repository abstracts the data access layer and provides
 * a clean interface for the domain layer to interact with user statistics.</p>
 * 
 * @author User Service Team
 * @version 1.0
 * @since 1.0.0
 */
public interface UserStatisticsRepository {
    
    /**
     * Saves user statistics.
     * 
     * @param userStatistics the user statistics to save
     * @return the saved user statistics
     */
    UserStatistics save(UserStatistics userStatistics);
    
    /**
     * Finds user statistics by user ID.
     * 
     * @param userId the user ID
     * @return optional containing user statistics if found
     */
    Optional<UserStatistics> findByUserId(Long userId);
    
    /**
     * Updates user statistics.
     * 
     * @param userStatistics the user statistics to update
     * @return the updated user statistics
     */
    UserStatistics update(UserStatistics userStatistics);
    
    /**
     * Deletes user statistics by user ID.
     * 
     * @param userId the user ID
     */
    void deleteByUserId(Long userId);
    
    /**
     * Checks if user statistics exist for the given user ID.
     * 
     * @param userId the user ID
     * @return true if statistics exist, false otherwise
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Increments the total task count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementTotalTasks(Long userId);
    
    /**
     * Increments the completed task count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementCompletedTasks(Long userId);
    
    /**
     * Increments the in-progress task count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementInProgressTasks(Long userId);
    
    /**
     * Increments the TODO task count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementTodoTasks(Long userId);
    
    /**
     * Decrements the in-progress task count and increments completed tasks for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics moveTaskFromInProgressToCompleted(Long userId);
    
    /**
     * Decrements the TODO task count and increments in-progress tasks for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics moveTaskFromTodoToInProgress(Long userId);
    
    /**
     * Increments the active project count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementActiveProjects(Long userId);
    
    /**
     * Increments the total project count for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics incrementTotalProjects(Long userId);
    
    /**
     * Updates the last activity timestamp for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics updateLastActivity(Long userId);
    
    /**
     * Updates the last task creation timestamp for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics updateLastTaskCreated(Long userId);
    
    /**
     * Updates the last task completion timestamp for a user.
     * 
     * @param userId the user ID
     * @return the updated user statistics
     */
    UserStatistics updateLastTaskCompleted(Long userId);
    
    /**
     * Deletes user statistics by ID.
     * 
     * @param id the statistics ID
     */
    void delete(Long id);
    
    /**
     * Saves multiple user statistics.
     * 
     * @param userStatisticsList the list of user statistics to save
     * @return the saved user statistics list
     */
    java.util.List<UserStatistics> save(java.util.List<UserStatistics> userStatisticsList);
    
    /**
     * Finds user statistics with completion rate greater than the specified value.
     * 
     * @param completionRate the minimum completion rate
     * @return list of user statistics with high completion rate
     */
    java.util.List<UserStatistics> findByCompletedTasksGreaterThan(int completedTasks);
    
    /**
     * Finds user statistics with TODO tasks greater than the specified value.
     * 
     * @param todoTasks the minimum number of TODO tasks
     * @return list of user statistics with many TODO tasks
     */
    java.util.List<UserStatistics> findByTodoTasksGreaterThan(int todoTasks);
}
