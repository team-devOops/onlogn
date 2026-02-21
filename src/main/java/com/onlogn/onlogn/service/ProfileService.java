package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import com.onlogn.onlogn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProfileService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPublicProfile(String slug) {
        UserEntity user = userRepository.findByProfileSlug(slug)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Profile not found",
                        "/api/v1/profiles/" + slug));

        long totalTasks = taskRepository.countByOwnerUserId(user.getId());
        long doneTasks = taskRepository.countByOwnerUserIdAndStatus(user.getId(), "done");
        long inProgressTasks = taskRepository.countByOwnerUserIdAndStatus(user.getId(), "in_progress");
        long todoTasks = taskRepository.countByOwnerUserIdAndStatus(user.getId(), "todo");

        Map<String, Object> statsSummary = Map.of(
                "total_tasks", totalTasks,
                "done_tasks", doneTasks,
                "in_progress_tasks", inProgressTasks,
                "todo_tasks", todoTasks
        );

        return Map.of(
                "profile_slug", user.getProfileSlug(),
                "display_name", user.getDisplayName(),
                "avatar_url", user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
                "bio", user.getBio() != null ? user.getBio() : "",
                "timezone", user.getTimezone(),
                "created_at", user.getCreatedAt().toString(),
                "stats_summary", statsSummary
        );
    }
}
