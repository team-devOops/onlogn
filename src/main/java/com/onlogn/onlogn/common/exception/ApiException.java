package com.onlogn.onlogn.common.exception;

import com.onlogn.onlogn.common.dto.ProblemResponse;

import java.util.List;

/**
 * API 전용 예외. RFC9457 Problem Details로 변환된다.
 */
public class ApiException extends RuntimeException {

    private final ProblemType problemType;
    private final String detail;
    private final String instance;
    private final List<ProblemResponse.ValidationErrorItem> errors;

    public ApiException(ProblemType problemType, String detail, String instance) {
        super(detail);
        this.problemType = problemType;
        this.detail = detail;
        this.instance = instance;
        this.errors = null;
    }

    public ApiException(ProblemType problemType, String detail, String instance, List<ProblemResponse.ValidationErrorItem> errors) {
        super(detail);
        this.problemType = problemType;
        this.detail = detail;
        this.instance = instance;
        this.errors = errors;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public List<ProblemResponse.ValidationErrorItem> getErrors() {
        return errors;
    }
}
