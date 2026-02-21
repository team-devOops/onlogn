package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.common.dto.ListMeta;
import com.onlogn.onlogn.entity.GroupEntity;
import com.onlogn.onlogn.service.GroupService;
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
            String icon
    ) {
    }

    @GetMapping
    public ResponseEntity<DataMetaEnvelope<List<GroupResponse>>> listGroups(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

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
    public ResponseEntity<DataMetaEnvelope<GroupResponse>> getGroup(
            @PathVariable("group_id") UUID groupId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GroupEntity group = groupService.getGroup(groupId, userId);
        return ResponseEntity.ok(DataMetaEnvelope.of(toGroupResponse(group)));
    }

    @PatchMapping("/{group_id}")
    public ResponseEntity<DataMetaEnvelope<GroupResponse>> updateGroup(
            @PathVariable("group_id") UUID groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GroupEntity group = groupService.updateGroup(
                groupId, userId, request.visibility(), request.description(),
                request.color(), request.icon());

        return ResponseEntity.ok(DataMetaEnvelope.of(toGroupResponse(group)));
    }

    @DeleteMapping("/{group_id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("group_id") UUID groupId) {
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
                group.getCreatedAt().toString(),
                group.getUpdatedAt().toString()
        );
    }
}
