package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.GroupEntity;
import com.onlogn.onlogn.repository.GroupRepository;
import com.onlogn.onlogn.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final TaskRepository taskRepository;

    public GroupService(GroupRepository groupRepository, TaskRepository taskRepository) {
        this.groupRepository = groupRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Page<GroupEntity> listGroups(UUID ownerUserId, int offset, int limit) {
        int page = offset / Math.max(limit, 1);
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        return groupRepository.findByOwnerUserId(ownerUserId, pageRequest);
    }

    @Transactional
    public GroupEntity createGroup(UUID ownerUserId, String visibility, String description, String color, String icon) {
        GroupEntity group = new GroupEntity();
        group.setOwnerUserId(ownerUserId);
        group.setVisibility(visibility != null ? visibility : "private");
        group.setDescription(description);
        group.setColor(color);
        group.setIcon(icon);
        return groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public GroupEntity getGroup(UUID groupId, UUID ownerUserId) {
        return groupRepository.findByIdAndOwnerUserId(groupId, ownerUserId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Group not found",
                        "/api/v1/groups/" + groupId));
    }

    @Transactional
    public GroupEntity updateGroup(UUID groupId, UUID ownerUserId,
                                   String visibility, String description, String color, String icon) {
        GroupEntity group = getGroup(groupId, ownerUserId);

        if (visibility != null) {
            group.setVisibility(visibility);
        }
        if (description != null) {
            group.setDescription(description);
        }
        if (color != null) {
            group.setColor(color);
        }
        if (icon != null) {
            group.setIcon(icon);
        }

        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(UUID groupId, UUID ownerUserId) {
        GroupEntity group = getGroup(groupId, ownerUserId);
        taskRepository.detachTasksFromGroup(group.getId());
        groupRepository.delete(group);
    }
}
