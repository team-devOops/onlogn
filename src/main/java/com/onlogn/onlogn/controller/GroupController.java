package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.entity.GroupEntity;
import com.onlogn.onlogn.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@Tag(name = "groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    public record GroupResponse(
            String id,
            @JsonProperty("owner_user_id") String ownerUserId,
            String visibility,
            String description,
            String color,
            String icon,
            @JsonProperty("sort_order") Integer sortOrder,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("updated_at") String updatedAt
    ) {
    }

    public record CreateGroupRequest(
            String visibility,
            String description,
            String color,
            String icon
    ) {
    }

    public record UpdateGroupRequest(
            String visibility,
            String description,
            String color,
            String icon,
            @JsonProperty("sort_order") Integer sortOrder
    ) {
    }

    @GetMapping
    @Operation(
            operationId = "listGroups",
            summary = "group 목록 조회",
            description = "인증 사용자를 위한 owner 범위 group 목록."
    )
    @ApiResponse(responseCode = "200", description = "group 목록 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<List<GroupResponse>>> listGroups(
            @Parameter(description = "페이지 오프셋") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "페이지 크기 (최대 100)") @RequestParam(defaultValue = "20") int limit) {

        limit = Math.min(limit, 100);
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<GroupEntity> page = groupService.listGroups(userId, offset, limit);

        List<GroupResponse> groups = page.getContent().stream()
                .map(this::toGroupResponse)
                .toList();

        ListMeta meta = ListMeta.of(offset, limit, page.getTotalElements());
        return ResponseEntity.ok(DataMetaEnvelope.of(groups, meta));
    }

    @PostMapping
    @Operation(
            operationId = "createGroup",
            summary = "group 생성",
            description = "새 owner 범위 group을 생성한다."
    )
    @ApiResponse(responseCode = "201", description = "group 생성 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "422", description = "RFC9457 validation problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GroupEntity group = groupService.createGroup(
                userId, request.visibility(), request.description(),
                request.color(), request.icon());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataMetaEnvelope.of(toGroupResponse(group)));
    }

    @GetMapping("/{group_id}")
    @Operation(
            operationId = "getGroup",
            summary = "group 조회",
            description = "owner 범위에서 단일 group을 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "group 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<GroupResponse>> getGroup(
            @Parameter(description = "group ID") @PathVariable("group_id") UUID groupId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GroupEntity group = groupService.getGroup(groupId, userId);
        return ResponseEntity.ok(DataMetaEnvelope.of(toGroupResponse(group)));
    }

    @PatchMapping("/{group_id}")
    @Operation(
            operationId = "updateGroup",
            summary = "group 수정",
            description = "owner 범위 group 메타데이터(visibility, description, color, icon)를 수정한다."
    )
    @ApiResponse(responseCode = "200", description = "group 수정 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "422", description = "RFC9457 validation problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<GroupResponse>> updateGroup(
            @Parameter(description = "group ID") @PathVariable("group_id") UUID groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GroupEntity group = groupService.updateGroup(
                groupId, userId, request.visibility(), request.description(),
                request.color(), request.icon(), request.sortOrder());

        return ResponseEntity.ok(DataMetaEnvelope.of(toGroupResponse(group)));
    }

    @DeleteMapping("/{group_id}")
    @Operation(
            operationId = "deleteGroup",
            summary = "group 삭제",
            description = "소유한 group을 삭제한다. 연결된 task는 group_id = null로 재할당된다."
    )
    @ApiResponse(responseCode = "204", description = "group 삭제 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "403", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "group ID") @PathVariable("group_id") UUID groupId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        groupService.deleteGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    private GroupResponse toGroupResponse(GroupEntity group) {
        return new GroupResponse(
                group.getId().toString(),
                group.getOwnerUserId().toString(),
                group.getVisibility(),
                group.getDescription(),
                group.getColor(),
                group.getIcon(),
                group.getSortOrder(),
                group.getCreatedAt().toString(),
                group.getUpdatedAt().toString()
        );
    }
}
