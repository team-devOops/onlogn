package com.onlogn.onlogn.repository;

import com.onlogn.onlogn.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<ReactionEntity, UUID> {
    Optional<ReactionEntity> findByUserIdAndTaskIdAndEmoji(UUID userId, UUID taskId, String emoji);
    List<ReactionEntity> findByTaskIdAndActiveTrue(UUID taskId);
    long countByTaskIdAndEmojiAndActiveTrue(UUID taskId, String emoji);
}
