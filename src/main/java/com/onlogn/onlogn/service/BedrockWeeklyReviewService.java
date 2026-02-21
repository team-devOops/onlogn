package com.onlogn.onlogn.service;

import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BedrockWeeklyReviewService {

    private static final Logger log = LoggerFactory.getLogger(BedrockWeeklyReviewService.class);

    private static final String SYSTEM_PROMPT = """
            아래는 최근 1주일간의 투두 기록이다.
            이 데이터를 바탕으로 "습관 분석 + 주간 회고"를 작성해줘.

            [분석 목표]
            - 어떤 습관이 생산성에 기여했는지
            - 어떤 습관이 지연/스트레스를 만들었는지
            - 다음 주에 바로 실천 가능한 개선안 도출

            [분석 규칙]
            - 완료율, 지연 패턴, 미완료 이월, 시간대/요일 편차를 우선 보라.
            - 반복 패턴 중심으로 해석하고, 단발성 이벤트는 별도 표기하라.
            - 비난하지 말고 실험 가능한 액션 중심으로 작성하라.
            - 한국어로 작성해라.

            [출력 형식]
            1) 이번 주 한줄 회고
            2) 잘한 점 3가지 (근거 포함)
            3) 막힌 점 3가지 (원인 포함)
            4) 반복 습관 패턴 5가지
               - 트리거(언제/상황) / 행동 / 결과
            5) 다음 주 실험 계획 3가지
               - 실험명 / 실행 조건 / 측정 지표(KPI)
            6) Stop / Start / Continue 각 3개
            7) 다음 주 체크리스트 7개 (하루 1개 실천용)

            [추가]
            - 마지막에 "이번 주 점수(10점 만점)"와 점수 근거 3줄을 작성해라.""";

    private final BedrockRuntimeClient client;
    private final String modelId;
    private final TaskRepository taskRepository;

    public BedrockWeeklyReviewService(
            BedrockRuntimeClient client,
            @Value("${app.bedrock.model-id:anthropic.claude-3-haiku-20240307-v1:0}") String modelId,
            TaskRepository taskRepository) {
        this.client = client;
        this.modelId = modelId;
        this.taskRepository = taskRepository;
    }

    public record WeeklyReview(String review, int taskCount, int doneCount, int todoCount, int inProgressCount, String periodFrom, String periodTo) {}

    public WeeklyReview generateWeeklyReview(UUID userId) {
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<TaskEntity> tasks = taskRepository.findByOwnerUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, oneWeekAgo);

        int taskCount = tasks.size();
        int doneCount = (int) tasks.stream().filter(t -> "done".equals(t.getStatus())).count();
        int todoCount = (int) tasks.stream().filter(t -> "todo".equals(t.getStatus())).count();
        int inProgressCount = (int) tasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count();

        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        String periodFrom = from.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String periodTo = to.format(DateTimeFormatter.ISO_LOCAL_DATE);

        if (tasks.isEmpty()) {
            return new WeeklyReview("최근 1주일간 등록된 투두가 없습니다.", 0, 0, 0, 0, periodFrom, periodTo);
        }

        String todoLogs = tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.joining("\n"));

        String reviewText = callBedrock(todoLogs);

        return new WeeklyReview(reviewText, taskCount, doneCount, todoCount, inProgressCount, periodFrom, periodTo);
    }

    private String formatTask(TaskEntity task) {
        StringBuilder sb = new StringBuilder();
        sb.append("- [").append(task.getStatus().toUpperCase()).append("] ").append(task.getTitle());
        if (task.getDueDate() != null) {
            sb.append(" (마감: ").append(task.getDueDate()).append(")");
        }
        if (task.getCreatedAt() != null) {
            LocalDate created = task.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
            sb.append(" [생성: ").append(created).append("]");
        }
        if (!task.getTags().isEmpty()) {
            sb.append(" #").append(String.join(" #", task.getTags()));
        }
        return sb.toString();
    }

    private String callBedrock(String todoLogs) {
        try {
            Message userMessage = Message.builder()
                    .role(ConversationRole.USER)
                    .content(ContentBlock.fromText("[입력 데이터]\n" + todoLogs))
                    .build();

            ConverseResponse response = client.converse(req -> req
                    .modelId(modelId)
                    .system(SystemContentBlock.builder().text(SYSTEM_PROMPT).build())
                    .messages(userMessage)
                    .inferenceConfig(InferenceConfiguration.builder()
                            .maxTokens(4096)
                            .temperature(0.3f)
                            .build()));

            return response.output().message().content().stream()
                    .filter(block -> block.text() != null)
                    .map(ContentBlock::text)
                    .findFirst()
                    .orElse("회고 생성에 실패했습니다.");
        } catch (Exception e) {
            log.warn("Bedrock weekly review failed: {}", e.getMessage());
            return "AI 회고 생성에 실패했습니다: " + e.getMessage();
        }
    }
}
