package com.onlogn.onlogn.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BedrockTagExtractor {

    private static final Logger log = LoggerFactory.getLogger(BedrockTagExtractor.class);

    private static final String SYSTEM_PROMPT = """
            당신은 투두리스트를 분석해, 작성자의 자기소개 프로필에 활용 가능한 핵심 문맥 태그를 추출하는 분석가입니다.

            [목표]
            - 작성자의 역량, 업무 성향, 관심사를 드러내는 문맥 태그를 최대 3개 추출

            [규칙]
            1) 태그는 최대 3개까지만 반환하세요. 근거가 약하면 1~2개만 반환하세요.
            2) 단순 작업명보다 프로필에 쓸 수 있는 상위 개념으로 추상화하세요.
            3) 프로젝트 고유명사, 제품명, 일회성 키워드는 제외하세요.
            4) 유사하거나 중복되는 의미는 하나로 통합하세요.
            5) 중요도가 높은 순서대로 정렬하세요.

            [출력 형식]
            - 꼭 한글 태그로 주세요
            - JSON만 반환하세요. 마크다운, 코드펜스, 부가 설명은 금지합니다.
            - 아래 구조를 정확히 지키세요:
            {"tags":[{"tag":"...","reason":"..."}]}""";

    private final BedrockRuntimeClient client;
    private final String modelId;

    public BedrockTagExtractor(
            BedrockRuntimeClient client,
            @Value("${app.bedrock.model-id:anthropic.claude-3-haiku-20240307-v1:0}") String modelId) {
        this.client = client;
        this.modelId = modelId;
    }

    public record ContextTag(String tag, String reason) {}

    public List<ContextTag> extractTags(String todoList) {
        try {
            Message userMessage = Message.builder()
                    .role(ConversationRole.USER)
                    .content(ContentBlock.fromText("[입력 투두리스트]\n" + todoList))
                    .build();

            ConverseResponse response = client.converse(req -> req
                    .modelId(modelId)
                    .system(SystemContentBlock.builder().text(SYSTEM_PROMPT).build())
                    .messages(userMessage)
                    .inferenceConfig(InferenceConfiguration.builder()
                            .maxTokens(500)
                            .temperature(0.0f)
                            .build()));

            String raw = response.output().message().content().stream()
                    .filter(block -> block.text() != null)
                    .map(ContentBlock::text)
                    .findFirst()
                    .orElse("");

            return parseContextTags(raw);
        } catch (Exception e) {
            log.warn("Bedrock tag extraction failed: {}", e.getMessage());
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private List<ContextTag> parseContextTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        try {
            String json = raw.strip();
            if (json.startsWith("```")) {
                json = json.replaceAll("^```[a-z]*\\n?", "").replaceAll("\\n?```$", "").strip();
            }
            tools.jackson.databind.ObjectMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();
            Map<String, Object> parsed = mapper.readValue(json, Map.class);
            List<Map<String, String>> tagsList = (List<Map<String, String>>) parsed.get("tags");
            if (tagsList == null) {
                return List.of();
            }
            List<ContextTag> result = new ArrayList<>();
            for (Map<String, String> entry : tagsList) {
                String tag = entry.getOrDefault("tag", "").strip();
                String reason = entry.getOrDefault("reason", "").strip();
                if (!tag.isEmpty()) {
                    result.add(new ContextTag(tag, reason));
                }
            }
            return result.size() > 3 ? result.subList(0, 3) : result;
        } catch (Exception e) {
            log.warn("Failed to parse Bedrock response as JSON: {}", e.getMessage());
            return List.of();
        }
    }
}
