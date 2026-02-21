package com.onlogn.onlogn.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RFC9457 Problem Details 응답 객체.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        @JsonProperty("request_id") String requestId,
        @JsonProperty("retry_after_seconds") Integer retryAfterSeconds,
        List<ValidationErrorItem> errors
) {

    public record ValidationErrorItem(String field, String reason) {}

    public static ProblemResponse of(String type, String title, int status, String detail, String instance) {
        return new ProblemResponse(type, title, status, detail, instance, null, null, null);
    }

    public static ProblemResponse withRequestId(String type, String title, int status, String detail, String instance, String requestId) {
        return new ProblemResponse(type, title, status, detail, instance, requestId, null, null);
    }

    public static ProblemResponse withRetryAfter(String type, String title, int status, String detail, String instance, int retryAfterSeconds) {
        return new ProblemResponse(type, title, status, detail, instance, null, retryAfterSeconds, null);
    }

    public static ProblemResponse withErrors(String type, String title, int status, String detail, String instance, List<ValidationErrorItem> errors) {
        return new ProblemResponse(type, title, status, detail, instance, null, null, errors);
    }
}
