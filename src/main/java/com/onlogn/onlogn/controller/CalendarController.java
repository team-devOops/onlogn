package com.onlogn.onlogn.controller;

import com.onlogn.onlogn.common.dto.CalendarMeta;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.service.CalendarService;
import com.onlogn.onlogn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/calendar")
@Tag(name = "calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final UserService userService;

    public CalendarController(CalendarService calendarService, UserService userService) {
        this.calendarService = calendarService;
        this.userService = userService;
    }

    @GetMapping("/monthly")
    @Operation(
            operationId = "getMonthlyTaskCalendar",
            summary = "월간 task 캘린더 조회",
            description = "요청한 (year, month)에 대해 0 값으로 채운 날짜를 포함해 완전한 일자 버킷을 반환한다.",
            tags = {"tasks", "calendar"}
    )
    @ApiResponse(responseCode = "200", description = "월간 캘린더 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<List<Map<String, Object>>>> getMonthlyCalendar(
            @Parameter(description = "조회 연도") @RequestParam int year,
            @Parameter(description = "조회 월 (1-12)") @RequestParam int month) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userService.getCurrentUser(userId);
        String userTimezone = user.getTimezone();
        List<Map<String, Object>> days = calendarService.getMonthlyCalendar(userId, year, month);
        CalendarMeta meta = new CalendarMeta(year, month, userTimezone);
        return ResponseEntity.ok(DataMetaEnvelope.of(days, meta));
    }
}
