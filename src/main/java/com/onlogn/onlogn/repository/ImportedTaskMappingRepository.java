package com.onlogn.onlogn.repository;

import com.onlogn.onlogn.entity.ImportedTaskMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportedTaskMappingRepository extends JpaRepository<ImportedTaskMappingEntity, UUID> {
    boolean existsByOwnerUserIdAndProviderAndProviderTaskId(UUID ownerUserId, String provider, String providerTaskId);
}
