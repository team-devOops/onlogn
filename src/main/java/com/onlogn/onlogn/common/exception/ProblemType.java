package com.onlogn.onlogn.common.exception;

/**
 * RFC9457 기준 문제 유형 카탈로그.
 */
public enum ProblemType {

    BAD_REQUEST("https://api.onlogn.com/problems/bad-request", "Bad Request", 400),
    AUTHENTICATION_REQUIRED("https://api.onlogn.com/problems/authentication-required", "Authentication Required", 401),
    PERMISSION_DENIED("https://api.onlogn.com/problems/permission-denied", "Permission Denied", 403),
    RESOURCE_NOT_FOUND("https://api.onlogn.com/problems/resource-not-found", "Resource Not Found", 404),
    STATE_CONFLICT("https://api.onlogn.com/problems/state-conflict", "State Conflict", 409),
    VALIDATION_FAILED("https://api.onlogn.com/problems/validation-failed", "Validation Failed", 422),
    RATE_LIMIT_EXCEEDED("https://api.onlogn.com/problems/rate-limit-exceeded", "Rate Limit Exceeded", 429),
    INTERNAL_SERVER_ERROR("https://api.onlogn.com/problems/internal-server-error", "Internal Server Error", 500);

    private final String type;
    private final String title;
    private final int status;

    ProblemType(String type, String title, int status) {
        this.type = type;
        this.title = title;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }
}
