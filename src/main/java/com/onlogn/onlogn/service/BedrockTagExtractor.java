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

@Service
public class BedrockTagExtractor {

    private static final Logger log = LoggerFactory.getLogger(BedrockTagExtractor.class);

    private static final String SYSTEM_PROMPT = """
            You are a tag extraction assistant. Given a task title (and optionally a description), \
            extract 1-5 short, lowercase, single-word or hyphenated tags that best categorize the task. \
            Return ONLY a comma-separated list of tags, nothing else. \
            Example input: "Design the new landing page for Q3 campaign" \
            Example output: design,landing-page,marketing,ui \
            If the input is too vague, return a single best-guess tag. \
            Never return empty. Never add explanations.""";

    private final BedrockRuntimeClient client;
    private final String modelId;

    public BedrockTagExtractor(
            BedrockRuntimeClient client,
            @Value("${app.bedrock.model-id:anthropic.claude-3-5-sonnet-20241022-v2:0}") String modelId) {
        this.client = client;
        this.modelId = modelId;
    }

    public List<String> extractTags(String title) {
        try {
            Message userMessage = Message.builder()
                    .role(ConversationRole.USER)
                    .content(ContentBlock.fromText("Task title: " + title))
                    .build();

            ConverseResponse response = client.converse(req -> req
                    .modelId(modelId)
                    .system(SystemContentBlock.builder().text(SYSTEM_PROMPT).build())
                    .messages(userMessage)
                    .inferenceConfig(InferenceConfiguration.builder()
                            .maxTokens(100)
                            .temperature(0.0f)
                            .build()));

            String raw = response.output().message().content().stream()
                    .filter(block -> block.text() != null)
                    .map(ContentBlock::text)
                    .findFirst()
                    .orElse("");

            return parseTags(raw);
        } catch (Exception e) {
            log.warn("Bedrock tag extraction failed for title='{}': {}", title, e.getMessage());
            return List.of();
        }
    }

    private List<String> parseTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> tags = new ArrayList<>();
        for (String part : raw.split(",")) {
            String tag = part.strip().toLowerCase().replaceAll("[^a-z0-9가-힣\\-]", "");
            if (!tag.isEmpty() && tag.length() <= 30) {
                tags.add(tag);
            }
        }
        return tags.size() > 5 ? tags.subList(0, 5) : tags;
    }
}
