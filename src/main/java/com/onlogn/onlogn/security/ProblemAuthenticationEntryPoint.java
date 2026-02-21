package com.onlogn.onlogn.security;

import tools.jackson.databind.ObjectMapper;
import com.onlogn.onlogn.common.dto.ProblemResponse;
import com.onlogn.onlogn.common.exception.ProblemType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProblemAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ProblemAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(401);
        response.setContentType("application/problem+json");
        ProblemResponse body = ProblemResponse.of(
                ProblemType.AUTHENTICATION_REQUIRED.getType(),
                ProblemType.AUTHENTICATION_REQUIRED.getTitle(),
                401,
                "Access token is missing or expired.",
                request.getRequestURI()
        );
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
