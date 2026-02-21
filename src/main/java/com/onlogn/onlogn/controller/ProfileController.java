package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.service.BedrockTagExtractor;
import com.onlogn.onlogn.service.BedrockTagExtractor.ContextTag;
import com.onlogn.onlogn.service.ProfileService;
import com.onlogn.onlogn.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
@Tag(name = "profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final BedrockTagExtractor bedrockTagExtractor;

    public ProfileController(ProfileService profileService, TaskService taskService,
                             UserRepository userRepository, TaskRepository taskRepository,
                             BedrockTagExtractor bedrockTagExtractor) {
        this.profileService = profileService;
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.bedrockTagExtractor = bedrockTagExtractor;
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
    @Operation(
            operationId = "getProfileBySlug",
            summary = "slug로 공개 프로필 조회",
            description = "지정된 slug에 대해 공개 allowlist 프로필 객체를 반환한다.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "공개 프로필 반환 성공.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> getProfile(
            @Parameter(description = "프로필 slug") @PathVariable String slug) {
        Map<String, Object> profile = profileService.getPublicProfile(slug);
        return ResponseEntity.ok(DataMetaEnvelope.of(profile));
    }

    @GetMapping("/{slug}/tasks")
    @Operation(
            operationId = "listProfileTasks",
            summary = "profile slug 기준 공개 task 목록 조회",
            description = "지정된 profile slug의 공개 task만 반환한다.\n서버가 public visibility 경계를 강제한다.",
            security = {},
            tags = {"profiles", "tasks"}
    )
    @ApiResponse(responseCode = "200", description = "공개 task 목록 반환 성공.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<List<TaskResponse>>> getProfileTasks(
            @Parameter(description = "프로필 slug") @PathVariable String slug,
            @Parameter(description = "페이지 오프셋") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "페이지 크기 (최대 100)") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "task 상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "group ID 필터") @RequestParam(required = false, name = "group_id") String groupId,
            @Parameter(description = "due_date 시작 범위") @RequestParam(required = false, name = "due_date_from") LocalDate dueDateFrom,
            @Parameter(description = "due_date 종료 범위") @RequestParam(required = false, name = "due_date_to") LocalDate dueDateTo) {

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

    public record ContextTagsResponse(List<ContextTag> tags) {}

    @PostMapping("/me/context-tags")
    @Operation(
            operationId = "extractMyContextTags",
            summary = "내 투두리스트 기반 프로필 문맥 태그 추출",
            description = "인증된 사용자의 전체 task 목록을 Bedrock AI에 전달하여 프로필용 문맥 태그를 최대 3개 추출한다.",
            tags = {"profiles", "tags"}
    )
    @ApiResponse(responseCode = "200", description = "태그 추출 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    public ResponseEntity<DataMetaEnvelope<ContextTagsResponse>> extractMyContextTags() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> titles = taskRepository.findAllTitlesByOwnerUserId(userId);
        if (titles.isEmpty()) {
            return ResponseEntity.ok(DataMetaEnvelope.of(new ContextTagsResponse(List.of())));
        }
        String todoList = String.join("\n", titles);
        List<ContextTag> tags = bedrockTagExtractor.extractTags(todoList);
        return ResponseEntity.ok(DataMetaEnvelope.of(new ContextTagsResponse(tags)));
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
