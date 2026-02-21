package com.onlogn.onlogn.controller;

import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.CalendarService;
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
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<DataMetaEnvelope<List<Map<String, Object>>>> getMonthlyCalendar(
            @RequestParam int year,
            @RequestParam int month) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Map<String, Object>> days = calendarService.getMonthlyCalendar(userId, year, month);
        return ResponseEntity.ok(DataMetaEnvelope.of(days));
    }
}
