package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.ReactionService;
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
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    public record ReactionRequest(@NotBlank String emoji) {
    }

    @GetMapping
    public ResponseEntity<DataMetaEnvelope<List<Map<String, Object>>>> listReactions(
            @PathVariable("task_id") UUID taskId) {
        UUID requesterId = getOptionalUserId();
        List<Map<String, Object>> reactions = reactionService.listReactions(taskId, requesterId);
        return ResponseEntity.ok(DataMetaEnvelope.of(reactions));
    }

    @PostMapping
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> toggleReaction(
            @PathVariable("task_id") UUID taskId,
            @Valid @RequestBody ReactionRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> result = reactionService.toggleReaction(taskId, userId, request.emoji());
        return ResponseEntity.ok(DataMetaEnvelope.of(result));
    }

    @DeleteMapping
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> removeReaction(
            @PathVariable("task_id") UUID taskId,
            @RequestParam String emoji) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
