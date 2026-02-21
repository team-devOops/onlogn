package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{task_id}/reactions")
@Tag(name = "reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    public record ReactionRequest(@NotBlank String emoji) {
    }

    @GetMapping
    @Operation(
            operationId = "listTaskReactions",
            summary = "공개 task reaction 목록 조회",
            description = "공개 task의 reaction 집계를 반환한다.",
            security = {},
            tags = {"tasks", "reactions"}
    )
    @ApiResponse(responseCode = "200", description = "reaction 목록 반환 성공.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<List<Map<String, Object>>>> listReactions(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId) {
        UUID requesterId = getOptionalUserId();
        List<Map<String, Object>> reactions = reactionService.listReactions(taskId, requesterId);
        return ResponseEntity.ok(DataMetaEnvelope.of(reactions));
    }

    @PostMapping
    @Operation(
            operationId = "toggleTaskReaction",
            summary = "공개 task reaction 추가",
            description = "공개 task에 emoji reaction을 추가한다.\n로그인 사용자는 (user_id, task_id, emoji) 조합으로 토글되고, 비로그인 사용자는 누를 때마다 +1 된다.",
            security = {},
            tags = {"tasks", "reactions"}
    )
    @ApiResponse(responseCode = "200", description = "reaction 추가/토글 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> toggleReaction(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId,
            @Valid @RequestBody ReactionRequest request) {
        UUID userId = getOptionalUserId();
        Map<String, Object> result = reactionService.toggleReaction(taskId, userId, request.emoji());
        return ResponseEntity.ok(DataMetaEnvelope.of(result));
    }

    @DeleteMapping
    @Operation(
            operationId = "removeTaskReaction",
            summary = "공개 task reaction 제거",
            description = "공개 task에서 현재 사용자의 특정 emoji reaction을 제거한다. 로그인 사용자만 자신의 reaction을 제거할 수 있다.",
            tags = {"tasks", "reactions"}
    )
    @ApiResponse(responseCode = "200", description = "reaction 제거 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "404", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> removeReaction(
            @Parameter(description = "task ID") @PathVariable("task_id") UUID taskId,
            @Parameter(description = "제거할 emoji") @RequestParam String emoji) {
        UUID userId = getOptionalUserId();
        if (userId == null) {
            // 비로그인 사용자는 제거 불가 — count만 반환
            long count = reactionService.countActiveReactions(taskId, emoji);
            return ResponseEntity.ok(DataMetaEnvelope.of(Map.of(
                    "task_id", taskId.toString(),
                    "emoji", emoji,
                    "requester_reacted", false,
                    "count", count
            )));
        }
        Map<String, Object> result = reactionService.removeReaction(taskId, userId, emoji);
        return ResponseEntity.ok(DataMetaEnvelope.of(result));
    }

    private UUID getOptionalUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
