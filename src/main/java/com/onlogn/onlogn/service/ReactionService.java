package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.ReactionEntity;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.repository.ReactionRepository;
import com.onlogn.onlogn.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final TaskRepository taskRepository;

    public ReactionService(ReactionRepository reactionRepository, TaskRepository taskRepository) {
        this.reactionRepository = reactionRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listReactions(UUID taskId, UUID requesterId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Task not found",
                        "/api/v1/tasks/" + taskId + "/reactions"));

        if (!"public".equals(task.getVisibility())) {
            throw new ApiException(
                    ProblemType.RESOURCE_NOT_FOUND,
                    "Task not found",
                    "/api/v1/tasks/" + taskId + "/reactions");
        }

        List<ReactionEntity> activeReactions = reactionRepository.findByTaskIdAndActiveTrue(taskId);

        Map<String, List<ReactionEntity>> grouped = new LinkedHashMap<>();
        for (ReactionEntity reaction : activeReactions) {
            grouped.computeIfAbsent(reaction.getEmoji(), k -> new ArrayList<>()).add(reaction);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ReactionEntity>> entry : grouped.entrySet()) {
            boolean requesterReacted = requesterId != null && entry.getValue().stream()
                    .anyMatch(r -> r.getUserId().equals(requesterId));

            result.add(Map.of(
                    "emoji", entry.getKey(),
                    "count", (long) entry.getValue().size(),
                    "requester_reacted", requesterReacted
            ));
        }

        return result;
    }

    @Transactional
    public Map<String, Object> toggleReaction(UUID taskId, UUID userId, String emoji) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Task not found",
                        "/api/v1/tasks/" + taskId + "/reactions"));

        if (!"public".equals(task.getVisibility())) {
            throw new ApiException(
                    ProblemType.RESOURCE_NOT_FOUND,
                    "Task not found",
                    "/api/v1/tasks/" + taskId + "/reactions");
        }

        boolean requesterReacted;

        if (userId != null) {
            ReactionEntity existing = reactionRepository.findByUserIdAndTaskIdAndEmoji(userId, taskId, emoji)
                    .orElse(null);

            if (existing != null) {
                existing.setActive(!existing.isActive());
                reactionRepository.save(existing);
                requesterReacted = existing.isActive();
            } else {
                ReactionEntity newReaction = new ReactionEntity();
                newReaction.setUserId(userId);
                newReaction.setTaskId(taskId);
                newReaction.setEmoji(emoji);
                newReaction.setActive(true);
                reactionRepository.save(newReaction);
                requesterReacted = true;
            }
        } else {
            ReactionEntity anonymous = new ReactionEntity();
            anonymous.setTaskId(taskId);
            anonymous.setEmoji(emoji);
            anonymous.setActive(true);
            reactionRepository.save(anonymous);
            requesterReacted = true;
        }

        long count = reactionRepository.countByTaskIdAndEmojiAndActiveTrue(taskId, emoji);

        return Map.of(
                "task_id", taskId.toString(),
                "emoji", emoji,
                "requester_reacted", requesterReacted,
                "count", count
        );
    }

    @Transactional(readOnly = true)
    public long countActiveReactions(UUID taskId, String emoji) {
        return reactionRepository.countByTaskIdAndEmojiAndActiveTrue(taskId, emoji);
    }

    @Transactional
    public Map<String, Object> removeReaction(UUID taskId, UUID userId, String emoji) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Task not found",
                        "/api/v1/tasks/" + taskId + "/reactions"));

        if (!"public".equals(task.getVisibility())) {
            throw new ApiException(
                    ProblemType.RESOURCE_NOT_FOUND,
                    "Task not found",
                    "/api/v1/tasks/" + taskId + "/reactions");
        }

        reactionRepository.findByUserIdAndTaskIdAndEmoji(userId, taskId, emoji)
                .ifPresent(reaction -> {
                    reaction.setActive(false);
                    reactionRepository.save(reaction);
                });

        long count = reactionRepository.countByTaskIdAndEmojiAndActiveTrue(taskId, emoji);

        return Map.of(
                "task_id", taskId.toString(),
                "emoji", emoji,
                "requester_reacted", false,
                "count", count
        );
    }
}
