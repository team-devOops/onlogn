package com.onlogn.onlogn.service;

import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CalendarService {

    private final TaskRepository taskRepository;

    public CalendarService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyCalendar(UUID ownerUserId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        Specification<TaskEntity> spec = TaskSpecification.build(
                ownerUserId, null, null, null, firstDay, lastDay);

        List<TaskEntity> tasks = taskRepository.findAll(spec);

        Map<LocalDate, List<TaskEntity>> tasksByDate = new HashMap<>();
        for (TaskEntity task : tasks) {
            if (task.getDueDate() != null) {
                tasksByDate.computeIfAbsent(task.getDueDate(), k -> new ArrayList<>()).add(task);
            }
        }

        List<Map<String, Object>> days = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            List<TaskEntity> dayTasks = tasksByDate.getOrDefault(date, List.of());

            long plannedCount = dayTasks.size();
            long doneCount = dayTasks.stream().filter(t -> "done".equals(t.getStatus())).count();
            long inProgressCount = dayTasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count();

            long completionRate = plannedCount > 0
                    ? Math.round((double) doneCount / plannedCount * 100)
                    : 0;

            days.add(Map.of(
                    "date", date.toString(),
                    "planned_count", plannedCount,
                    "done_count", doneCount,
                    "in_progress_count", inProgressCount,
                    "completion_rate", completionRate
            ));
        }

        return days;
    }
}
