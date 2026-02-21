package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.service.ProfileService;
import com.onlogn.onlogn.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final TaskService taskService;
    private final UserRepository userRepository;

    public ProfileController(ProfileService profileService, TaskService taskService,
                             UserRepository userRepository) {
        this.profileService = profileService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    public record TaskResponse(
            String id,
            @JsonProperty("owner_user_id") String ownerUserId,
            @JsonProperty("group_id") String groupId,
            String title,
            String status,
            String visibility,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("updated_at") String updatedAt,
            @JsonProperty("due_date") String dueDate,
            @JsonProperty("start_time") String startTime,
            @JsonProperty("end_time") String endTime,
            List<String> tags,
            @JsonProperty("reference_links") List<String> referenceLinks
    ) {
    }

    @GetMapping("/{slug}")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> getProfile(@PathVariable String slug) {
        Map<String, Object> profile = profileService.getPublicProfile(slug);
        return ResponseEntity.ok(DataMetaEnvelope.of(profile));
    }

    @GetMapping("/{slug}/tasks")
    public ResponseEntity<DataMetaEnvelope<List<TaskResponse>>> getProfileTasks(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "group_id") String groupId,
            @RequestParam(required = false, name = "due_date_from") LocalDate dueDateFrom,
            @RequestParam(required = false, name = "due_date_to") LocalDate dueDateTo) {

        limit = Math.min(limit, 100);

        UserEntity user = userRepository.findByProfileSlug(slug)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Profile not found",
                        "/api/v1/profiles/" + slug + "/tasks"));

        Page<TaskEntity> page = taskService.listPublicTasks(
                user.getId(), status, groupId, dueDateFrom, dueDateTo, offset, limit);

        List<TaskResponse> tasks = page.getContent().stream()
                .map(this::toTaskResponse)
                .toList();

        ListMeta meta = ListMeta.of(offset, limit, page.getTotalElements());
        return ResponseEntity.ok(DataMetaEnvelope.of(tasks, meta));
    }

    private TaskResponse toTaskResponse(TaskEntity task) {
        return new TaskResponse(
                task.getId().toString(),
                task.getOwnerUserId().toString(),
                task.getGroupId() != null ? task.getGroupId().toString() : null,
                task.getTitle(),
                task.getStatus(),
                task.getVisibility(),
                task.getCreatedAt().toString(),
                task.getUpdatedAt().toString(),
                task.getDueDate() != null ? task.getDueDate().toString() : null,
                task.getStartTime() != null ? task.getStartTime().toString() : null,
                task.getEndTime() != null ? task.getEndTime().toString() : null,
                task.getTags(),
                task.getReferenceLinks()
        );
    }
}
