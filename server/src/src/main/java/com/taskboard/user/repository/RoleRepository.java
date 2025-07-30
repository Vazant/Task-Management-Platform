package com.taskboard.user.repository;

import com.taskboard.user.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for RoleEntity.
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Find a role by name.
     *
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<RoleEntity> findByName(String name);

    /**
     * Check if a role exists by name.
     *
     * @param name the role name
     * @return true if role exists
     */
    boolean existsByName(String name);
}
