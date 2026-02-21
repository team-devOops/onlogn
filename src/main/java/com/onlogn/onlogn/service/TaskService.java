package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final BedrockTagExtractor bedrockTagExtractor;

    public TaskService(TaskRepository taskRepository, BedrockTagExtractor bedrockTagExtractor) {
        this.taskRepository = taskRepository;
        this.bedrockTagExtractor = bedrockTagExtractor;
    }

    @Transactional(readOnly = true)
    public Page<TaskEntity> listTasks(UUID ownerUserId, String status, String visibility,
                                      String groupId, LocalDate dueDateFrom, LocalDate dueDateTo,
                                      int offset, int limit) {
        Specification<TaskEntity> spec = TaskSpecification.build(
                ownerUserId, status, visibility, groupId, dueDateFrom, dueDateTo);

        int page = offset / Math.max(limit, 1);
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        return taskRepository.findAll(spec, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<TaskEntity> listPublicTasks(UUID ownerUserId, String status, String groupId,
                                            LocalDate dueDateFrom, LocalDate dueDateTo,
                                            int offset, int limit) {
        Specification<TaskEntity> spec = TaskSpecification.build(
                ownerUserId, status, "public", groupId, dueDateFrom, dueDateTo);

        int page = offset / Math.max(limit, 1);
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        return taskRepository.findAll(spec, pageRequest);
    }

    @Transactional
    public TaskEntity createTask(UUID ownerUserId, String title, UUID groupId, String status,
                                 String visibility, LocalDate dueDate, Instant startTime,
                                 Instant endTime, List<String> tags, List<String> referenceLinks) {
        TaskEntity task = new TaskEntity();
        task.setOwnerUserId(ownerUserId);
        task.setTitle(title);
        task.setGroupId(groupId);
        task.setStatus(status != null ? status : "todo");
        task.setVisibility(visibility != null ? visibility : "private");
        task.setDueDate(dueDate);
        task.setStartTime(startTime);
        task.setEndTime(endTime);
        if (tags != null && !tags.isEmpty()) {
            task.setTags(tags);
        } else {
            List<String> aiTags = bedrockTagExtractor.extractTags(title).stream()
                    .map(BedrockTagExtractor.ContextTag::tag)
                    .toList();
            if (!aiTags.isEmpty()) {
                task.setTags(aiTags);
            }
        }
        if (referenceLinks != null) {
            task.setReferenceLinks(referenceLinks);
        }
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public TaskEntity getTask(UUID taskId, UUID ownerUserId) {
        return taskRepository.findByIdAndOwnerUserId(taskId, ownerUserId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Task not found",
                        "/api/v1/tasks/" + taskId));
    }

    @Transactional
    public TaskEntity updateTask(UUID taskId, UUID ownerUserId,
                                 String title, UUID groupId, Boolean clearGroupId,
                                 String status, String visibility,
                                 LocalDate dueDate, Instant startTime, Instant endTime,
                                 List<String> tags, List<String> referenceLinks) {
        TaskEntity task = getTask(taskId, ownerUserId);

        if (title != null) {
            task.setTitle(title);
        }
        if (Boolean.TRUE.equals(clearGroupId)) {
            task.setGroupId(null);
        } else if (groupId != null) {
            task.setGroupId(groupId);
        }
        if (status != null) {
            task.setStatus(status);
        }
        if (visibility != null) {
            task.setVisibility(visibility);
        }
        if (dueDate != null) {
            task.setDueDate(dueDate);
        }
        if (startTime != null) {
            task.setStartTime(startTime);
        }
        if (endTime != null) {
            task.setEndTime(endTime);
        }
        if (tags != null) {
            task.setTags(tags);
        }
        if (referenceLinks != null) {
            task.setReferenceLinks(referenceLinks);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID taskId, UUID ownerUserId) {
        TaskEntity task = getTask(taskId, ownerUserId);
        taskRepository.delete(task);
    }
}
