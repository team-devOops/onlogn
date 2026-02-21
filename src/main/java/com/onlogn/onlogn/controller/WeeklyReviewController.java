package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.BedrockWeeklyReviewService;
import com.onlogn.onlogn.service.BedrockWeeklyReviewService.WeeklyReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "reviews", description = "AI 기반 주간 회고 API.")
public class WeeklyReviewController {

    private final BedrockWeeklyReviewService weeklyReviewService;

    public WeeklyReviewController(BedrockWeeklyReviewService weeklyReviewService) {
        this.weeklyReviewService = weeklyReviewService;
    }

    public record WeeklyReviewResponse(
            String review,
            @JsonProperty("task_count") int taskCount,
            @JsonProperty("done_count") int doneCount,
            @JsonProperty("todo_count") int todoCount,
            @JsonProperty("in_progress_count") int inProgressCount,
            @JsonProperty("period_from") String periodFrom,
            @JsonProperty("period_to") String periodTo
    ) {}

    @PostMapping("/weekly")
    @Operation(
            operationId = "generateWeeklyReview",
            summary = "최근 1주일 습관 분석 + 주간 회고 생성",
            description = "인증된 사용자의 최근 1주일 task 기록을 Bedrock AI에 전달하여 습관 분석과 주간 회고를 생성한다."
    )
    @ApiResponse(responseCode = "200", description = "주간 회고 생성 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    public ResponseEntity<DataMetaEnvelope<WeeklyReviewResponse>> generateWeeklyReview() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WeeklyReview result = weeklyReviewService.generateWeeklyReview(userId);
        WeeklyReviewResponse response = new WeeklyReviewResponse(
                result.review(),
                result.taskCount(),
                result.doneCount(),
                result.todoCount(),
                result.inProgressCount(),
                result.periodFrom(),
                result.periodTo()
        );
        return ResponseEntity.ok(DataMetaEnvelope.of(response));
    }
}
