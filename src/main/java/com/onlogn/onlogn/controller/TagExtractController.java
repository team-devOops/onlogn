package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.BedrockTagExtractor;
import com.onlogn.onlogn.service.BedrockTagExtractor.ContextTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@Tag(name = "tags", description = "AI 태그 추출 테스트 API.")
public class TagExtractController {

    private final BedrockTagExtractor bedrockTagExtractor;

    public TagExtractController(BedrockTagExtractor bedrockTagExtractor) {
        this.bedrockTagExtractor = bedrockTagExtractor;
    }

    public record ExtractRequest(@NotBlank @JsonProperty("todo_list") String todoList) {}

    public record ExtractResponse(@JsonProperty("todo_list") String todoList, List<ContextTag> tags) {}

    @PostMapping("/extract")
    @Operation(
            operationId = "extractTags",
            summary = "투두리스트 기반 프로필 문맥 태그 추출",
            description = "Bedrock Claude 3.5 Sonnet을 호출하여 투두리스트에서 작성자의 역량/성향/관심사를 드러내는 문맥 태그를 최대 3개 추출한다. 인증 불필요.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "태그 추출 성공.")
    @ApiResponse(responseCode = "400", description = "todo_list가 비어있음.")
    public ResponseEntity<DataMetaEnvelope<ExtractResponse>> extractTags(
            @Valid @RequestBody ExtractRequest request) {
        List<ContextTag> tags = bedrockTagExtractor.extractTags(request.todoList());
        return ResponseEntity.ok(DataMetaEnvelope.of(new ExtractResponse(request.todoList(), tags)));
    }
}
