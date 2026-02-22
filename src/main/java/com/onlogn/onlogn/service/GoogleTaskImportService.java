package com.onlogn.onlogn.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.ImportedTaskMappingEntity;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.repository.ImportedTaskMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GoogleTaskImportService {

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024 * 1024;
    private static final String PROVIDER = "google_tasks";

    private final ObjectMapper objectMapper;
    private final TaskService taskService;
    private final ImportedTaskMappingRepository importedTaskMappingRepository;

    public GoogleTaskImportService(ObjectMapper objectMapper,
                                   TaskService taskService,
                                   ImportedTaskMappingRepository importedTaskMappingRepository) {
        this.objectMapper = objectMapper;
        this.taskService = taskService;
        this.importedTaskMappingRepository = importedTaskMappingRepository;
    }

    public record ImportResult(int createdCount, int skippedCount, int failedCount) {
    }

    @Transactional
    public ImportResult importFromJson(UUID userId, MultipartFile file) {
        validateUpload(file);

        JsonNode root = parseJson(file);
        List<JsonNode> taskNodes = new ArrayList<>();
        collectTaskNodes(root, taskNodes);

        int created = 0;
        int skipped = 0;
        int failed = 0;
        Set<String> seenProviderTaskIds = new HashSet<>();

        for (JsonNode taskNode : taskNodes) {
            String providerTaskId = asText(taskNode, "id");
            String title = asText(taskNode, "title");

            if (providerTaskId == null || title == null) {
                failed += 1;
                continue;
            }
            if (!seenProviderTaskIds.add(providerTaskId)) {
                skipped += 1;
                continue;
            }

            boolean deleted = taskNode.path("deleted").asBoolean(false);
            boolean hidden = taskNode.path("hidden").asBoolean(false);
            if (deleted || hidden) {
                skipped += 1;
                continue;
            }

            boolean exists = importedTaskMappingRepository.existsByOwnerUserIdAndProviderAndProviderTaskId(
                    userId,
                    PROVIDER,
                    providerTaskId
            );
            if (exists) {
                skipped += 1;
                continue;
            }

            try {
                String mappedStatus = "completed".equals(taskNode.path("status").asText()) ? "done" : "todo";
                TaskEntity createdTask = taskService.createTask(
                        userId,
                        title,
                        null,
                        mappedStatus,
                        "private",
                        null,
                        null,
                        null,
                        null,
                        null
                );

                ImportedTaskMappingEntity mapping = new ImportedTaskMappingEntity();
                mapping.setOwnerUserId(userId);
                mapping.setProvider(PROVIDER);
                mapping.setProviderTaskId(providerTaskId);
                mapping.setTaskId(createdTask.getId());
                importedTaskMappingRepository.save(mapping);
                created += 1;
            } catch (Exception ignore) {
                failed += 1;
            }
        }

        return new ImportResult(created, skipped, failed);
    }

    private void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    ProblemType.BAD_REQUEST,
                    "Import file is required",
                    "/api/v1/tasks/import/google"
            );
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ApiException(
                    ProblemType.BAD_REQUEST,
                    "File size exceeds 2GB limit",
                    "/api/v1/tasks/import/google"
            );
        }
    }

    private JsonNode parseJson(MultipartFile file) {
        try {
            return objectMapper.readTree(file.getInputStream());
        } catch (IOException e) {
            throw new ApiException(
                    ProblemType.BAD_REQUEST,
                    "Invalid JSON file",
                    "/api/v1/tasks/import/google"
            );
        }
    }

    private void collectTaskNodes(JsonNode node, List<JsonNode> out) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isObject()) {
            if (looksLikeTask(node)) {
                out.add(node);
            }
            for (JsonNode child : node) {
                collectTaskNodes(child, out);
            }
            return;
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                collectTaskNodes(child, out);
            }
        }
    }

    private boolean looksLikeTask(JsonNode node) {
        String kind = asText(node, "kind");
        if ("tasks#task".equals(kind)) {
            return true;
        }
        if ("tasks#taskLists".equals(kind) || "tasks#tasks".equals(kind)) {
            return false;
        }
        return node.hasNonNull("id") && node.hasNonNull("title") && node.has("status");
    }

    private String asText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        String text = value.asText();
        if (text == null) {
            return null;
        }
        text = text.trim();
        return text.isEmpty() ? null : text;
    }
}
