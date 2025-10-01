package com.taskboard.api.repository;

import com.taskboard.api.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByCreatedBy(String createdBy);
    
    List<Project> findByStatus(String status);
    
    List<Project> findByPriority(String priority);
    
    @Query("SELECT p FROM Project p WHERE p.createdBy = :createdBy AND p.status = :status")
    List<Project> findByCreatedByAndStatus(@Param("createdBy") String createdBy, @Param("status") String status);
    
    @Query("SELECT p FROM Project p WHERE p.createdBy = :createdBy AND p.name LIKE %:name%")
    List<Project> findByCreatedByAndNameContaining(@Param("createdBy") String createdBy, @Param("name") String name);
}

