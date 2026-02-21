package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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

    public record CreateTaskRequest(
            @NotBlank String title,
            @JsonProperty("group_id") UUID groupId,
            String status,
            String visibility,
            @JsonProperty("due_date") LocalDate dueDate,
            @JsonProperty("start_time") Instant startTime,
            @JsonProperty("end_time") Instant endTime,
            List<String> tags,
            @JsonProperty("reference_links") List<String> referenceLinks
    ) {
    }

    public record UpdateTaskRequest(
            String title,
            @JsonProperty("group_id") UUID groupId,
            @JsonProperty("clear_group_id") Boolean clearGroupId,
            String status,
            String visibility,
            @JsonProperty("due_date") LocalDate dueDate,
            @JsonProperty("start_time") Instant startTime,
            @JsonProperty("end_time") Instant endTime,
            List<String> tags,
            @JsonProperty("reference_links") List<String> referenceLinks
    ) {
    }

    @GetMapping
    public ResponseEntity<DataMetaEnvelope<List<TaskResponse>>> listTasks(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false, name = "group_id") String groupId,
            @RequestParam(required = false, name = "due_date_from") LocalDate dueDateFrom,
            @RequestParam(required = false, name = "due_date_to") LocalDate dueDateTo) {

        limit = Math.min(limit, 100);
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<TaskEntity> page = taskService.listTasks(
                userId, status, visibility, groupId, dueDateFrom, dueDateTo, offset, limit);

        List<TaskResponse> tasks = page.getContent().stream()
                .map(this::toTaskResponse)
                .toList();

        ListMeta meta = ListMeta.of(offset, limit, page.getTotalElements());
        return ResponseEntity.ok(DataMetaEnvelope.of(tasks, meta));
    }

    @PostMapping
    public ResponseEntity<DataMetaEnvelope<TaskResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskEntity task = taskService.createTask(
                userId, request.title(), request.groupId(), request.status(),
                request.visibility(), request.dueDate(), request.startTime(),
                request.endTime(), request.tags(), request.referenceLinks());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataMetaEnvelope.of(toTaskResponse(task)));
    }

    @GetMapping("/{task_id}")
    public ResponseEntity<DataMetaEnvelope<TaskResponse>> getTask(
            @PathVariable("task_id") UUID taskId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskEntity task = taskService.getTask(taskId, userId);
        return ResponseEntity.ok(DataMetaEnvelope.of(toTaskResponse(task)));
    }

    @PatchMapping("/{task_id}")
    public ResponseEntity<DataMetaEnvelope<TaskResponse>> updateTask(
            @PathVariable("task_id") UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskEntity task = taskService.updateTask(
                taskId, userId, request.title(), request.groupId(), request.clearGroupId(),
                request.status(), request.visibility(), request.dueDate(),
                request.startTime(), request.endTime(), request.tags(), request.referenceLinks());

        return ResponseEntity.ok(DataMetaEnvelope.of(toTaskResponse(task)));
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("task_id") UUID taskId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
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
