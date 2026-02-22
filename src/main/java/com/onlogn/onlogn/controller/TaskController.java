package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.service.GoogleTaskImportService;
import com.onlogn.onlogn.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "tasks")
public class TaskController {

    private final TaskService taskService;
    private final GoogleTaskImportService googleTaskImportService;

    public TaskController(TaskService taskService, GoogleTaskImportService googleTaskImportService) {
        this.taskService = taskService;
        this.googleTaskImportService = googleTaskImportService;
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

    public record ImportTasksResponse(
            @JsonProperty("created_count") int createdCount,
            @JsonProperty("skipped_count") int skippedCount,
            @JsonProperty("failed_count") int failedCount
    ) {
    }

    @GetMapping
    @Operation(
            operationId = "listTasks",
            summary = "owner task 목록 조회",
            description = "인증 사용자를 위한 owner 범위 task 목록.\n기준 정렬은 created_at desc, 타이브레이커는 id desc다."
    )
    @ApiResponse(responseCode = "200", description = "task 목록 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<List<TaskResponse>>> listTasks(
            @Parameter(description = "페이지 오프셋") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "페이지 크기 (최대 100)") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "task 상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "visibility 필터") @RequestParam(required = false) String visibility,
            @Parameter(description = "group ID 필터") @RequestParam(required = false, name = "group_id") String groupId,
            @Parameter(description = "due_date 시작 범위") @RequestParam(required = false, name = "due_date_from") LocalDate dueDateFrom,
            @Parameter(description = "due_date 종료 범위") @RequestParam(required = false, name = "due_date_to") LocalDate dueDateTo) {

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
    @Operation(
            operationId = "createTask",
            summary = "owner task 생성",
            description = "인증 owner 범위에서 task를 생성한다."
    )
    @ApiResponse(responseCode = "201", description = "task 생성 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "422", description = "RFC9457 validation problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
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
    @Operation(
            operationId = "getTask",
            summary = "owner task 조회",
            description = "owner 범위 단일 task를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "task 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<TaskResponse>> getTask(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskEntity task = taskService.getTask(taskId, userId);
        return ResponseEntity.ok(DataMetaEnvelope.of(toTaskResponse(task)));
    }

    @PatchMapping("/{task_id}")
    @Operation(
            operationId = "updateTask",
            summary = "owner task 수정",
            description = "owner 범위 task의 변경 가능한 필드를 수정한다."
    )
    @ApiResponse(responseCode = "200", description = "task 수정 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "422", description = "RFC9457 validation problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<TaskResponse>> updateTask(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskEntity task = taskService.updateTask(
                taskId, userId, request.title(), request.groupId(), request.clearGroupId(),
                request.status(), request.visibility(), request.dueDate(),
                request.startTime(), request.endTime(), request.tags(), request.referenceLinks());

        return ResponseEntity.ok(DataMetaEnvelope.of(toTaskResponse(task)));
    }

    @DeleteMapping("/{task_id}")
    @Operation(
            operationId = "deleteTask",
            summary = "owner task 삭제",
            description = "owner 범위 task를 삭제한다."
    )
    @ApiResponse(responseCode = "204", description = "task 삭제 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import/google", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            operationId = "importGoogleTasks",
            summary = "Google Tasks JSON 가져오기",
            description = "단일 JSON 파일을 업로드해 Google Tasks를 1레벨 task로 가져온다."
    )
    @ApiResponse(responseCode = "201", description = "가져오기 성공.")
    @ApiResponse(responseCode = "400", description = "잘못된 파일/요청.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<ImportTasksResponse>> importGoogleTasks(
            @RequestParam("file") MultipartFile file) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GoogleTaskImportService.ImportResult result = googleTaskImportService.importFromJson(userId, file);
        ImportTasksResponse response = new ImportTasksResponse(
                result.createdCount(),
                result.skippedCount(),
                result.failedCount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(DataMetaEnvelope.of(response));
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
