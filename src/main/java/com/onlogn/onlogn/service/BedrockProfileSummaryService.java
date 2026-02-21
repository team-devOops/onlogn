package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.TaskEntity;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.TaskRepository;
import com.onlogn.onlogn.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BedrockProfileSummaryService {

    private static final Logger log = LoggerFactory.getLogger(BedrockProfileSummaryService.class);

    private static final String SYSTEM_PROMPT = """
            아래는 사용자의 투두 기록이다.
            이 데이터를 바탕으로 외부 공개용 "실행 프로필 요약"을 작성해줘.

            [핵심 목적]
            - 채용/협업/포트폴리오 소개에 활용 가능한 공개용 프로필 생성
            - 업무 방식과 강점을 간결하게 전달

            [안전/공개 기준]
            - 개인식별정보(이름, 이메일, 전화번호, 주소, 계정, 조직 내부 식별자)를 절대 출력하지 마라.
            - 고객명, 회사명, 서비스명, 저장소명, 링크, 내부 용어는 일반화해서 표현해라.
            - 보안/비공개 정보(일정 세부, 매출, 계약, 계정 권한, 내부 이슈)는 제외해라.
            - 부정적 표현은 피하고, 개선점은 중립적 성장 포인트로 표현해라.

            [분석 규칙]
            - 기록에 있는 사실만 근거로 작성하고 추측은 최소화해라.
            - 반복적으로 나타난 패턴(최소 2회 이상)을 우선 반영해라.
            - 근거가 약한 내용은 제외하거나 "근거 부족"으로 표시해라.
            - 한국어로 작성해라.

            [출력 형식]
            1) 공개 프로필 한 줄 소개 (40~80자)
            2) 공개용 요약 문단 (120~180자)
            3) 핵심 역량 5가지 (각 1문장)
            4) 업무 스타일 3가지 (협업/실행/문제해결 관점)
            5) 최근 성과 키워드 TOP 5 (키워드 + 근거 1줄)
            6) 공개 프로필 태그 8개 (해시태그 형태, 일반명사 위주)

            [톤]
            - 전문적이고 신뢰감 있는 톤
            - 간결하고 과장 없는 표현""";

    private final BedrockRuntimeClient client;
    private final String modelId;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public BedrockProfileSummaryService(
            BedrockRuntimeClient client,
            @Value("${app.bedrock.model-id:anthropic.claude-3-haiku-20240307-v1:0}") String modelId,
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.client = client;
        this.modelId = modelId;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public record ProfileSummary(String summary, int totalTasks, int doneTasks) {}

    public ProfileSummary generateProfileSummary(String slug) {
        UserEntity user = userRepository.findByProfileSlug(slug)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "Profile not found",
                        "/api/v1/profiles/" + slug + "/summary"));

        UUID userId = user.getId();
        Specification<TaskEntity> spec = TaskSpecification.build(userId, null, "public", null, null, null);
        List<TaskEntity> tasks = taskRepository.findAll(spec);

        int totalTasks = tasks.size();
        int doneTasks = (int) tasks.stream().filter(t -> "done".equals(t.getStatus())).count();

        if (tasks.isEmpty()) {
            return new ProfileSummary("공개된 task 기록이 없습니다.", 0, 0);
        }

        String todoLogs = tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.joining("\n"));

        String summaryText = callBedrock(todoLogs);
        return new ProfileSummary(summaryText, totalTasks, doneTasks);
    }

    public ProfileSummary generateMyProfileSummary(UUID userId) {
        Specification<TaskEntity> spec = TaskSpecification.build(userId, null, null, null, null, null);
        List<TaskEntity> tasks = taskRepository.findAll(spec);

        int totalTasks = tasks.size();
        int doneTasks = (int) tasks.stream().filter(t -> "done".equals(t.getStatus())).count();

        if (tasks.isEmpty()) {
            return new ProfileSummary("등록된 task 기록이 없습니다.", 0, 0);
        }

        String todoLogs = tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.joining("\n"));

        String summaryText = callBedrock(todoLogs);
        return new ProfileSummary(summaryText, totalTasks, doneTasks);
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
                            .temperature(0.2f)
                            .build()));

            return response.output().message().content().stream()
                    .filter(block -> block.text() != null)
                    .map(ContentBlock::text)
                    .findFirst()
                    .orElse("프로필 요약 생성에 실패했습니다.");
        } catch (Exception e) {
            log.warn("Bedrock profile summary failed: {}", e.getMessage());
            return "AI 프로필 요약 생성에 실패했습니다: " + e.getMessage();
        }
    }
}
