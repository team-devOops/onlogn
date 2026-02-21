package com.onlogn.onlogn.repository;

import com.onlogn.onlogn.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID>, JpaSpecificationExecutor<TaskEntity> {
    Optional<TaskEntity> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);

    long countByOwnerUserId(UUID ownerUserId);
    long countByOwnerUserIdAndStatus(UUID ownerUserId, String status);

    @Query("SELECT t.title FROM TaskEntity t WHERE t.ownerUserId = :ownerUserId ORDER BY t.createdAt DESC")
    List<String> findAllTitlesByOwnerUserId(UUID ownerUserId);

    List<TaskEntity> findByOwnerUserIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID ownerUserId, Instant after);

    List<TaskEntity> findByOwnerUserIdAndVisibilityAndCreatedAtAfterOrderByCreatedAtDesc(UUID ownerUserId, String visibility, Instant after);

    @Modifying
    @Query("UPDATE TaskEntity t SET t.groupId = null WHERE t.groupId = :groupId")
    void detachTasksFromGroup(UUID groupId);
}
