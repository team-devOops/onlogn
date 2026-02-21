package com.onlogn.onlogn.repository;

import com.onlogn.onlogn.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    Page<GroupEntity> findByOwnerUserId(UUID ownerUserId, Pageable pageable);
    Optional<GroupEntity> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);
}
