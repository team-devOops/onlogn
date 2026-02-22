package com.onlogn.onlogn.common.exception;

import com.onlogn.onlogn.common.dto.ProblemResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemResponse> handleApiException(ApiException ex) {
        ProblemType pt = ex.getProblemType();
        ProblemResponse body;
        if (ex.getErrors() != null && !ex.getErrors().isEmpty()) {
            body = ProblemResponse.withErrors(pt.getType(), pt.getTitle(), pt.getStatus(), ex.getDetail(), ex.getInstance(), ex.getErrors());
        } else {
            body = ProblemResponse.of(pt.getType(), pt.getTitle(), pt.getStatus(), ex.getDetail(), ex.getInstance());
        }
        return ResponseEntity.status(pt.getStatus()).contentType(PROBLEM_JSON).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ProblemResponse.ValidationErrorItem(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ProblemResponse body = ProblemResponse.withErrors(
                ProblemType.VALIDATION_FAILED.getType(),
                ProblemType.VALIDATION_FAILED.getTitle(),
                422,
                "Request body validation failed.",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(422).contentType(PROBLEM_JSON).body(body);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ProblemResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        ProblemResponse body = ProblemResponse.of(
                ProblemType.BAD_REQUEST.getType(),
                ProblemType.BAD_REQUEST.getTitle(),
                400,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(400).contentType(PROBLEM_JSON).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) throws NoResourceFoundException {
        throw ex;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) throws ResponseStatusException {
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        String requestId = "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        log.error("Unhandled exception [{}]: {}", requestId, ex.getMessage(), ex);
        ProblemResponse body = ProblemResponse.withRequestId(
                ProblemType.INTERNAL_SERVER_ERROR.getType(),
                ProblemType.INTERNAL_SERVER_ERROR.getTitle(),
                500,
                "An unexpected error occurred while processing the request.",
                request.getRequestURI(),
                requestId
        );
        return ResponseEntity.status(500).contentType(PROBLEM_JSON).body(body);
    }
}
