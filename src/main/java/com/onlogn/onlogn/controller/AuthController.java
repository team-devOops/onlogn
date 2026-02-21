package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.AuthService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record GithubExchangeRequest(DataWrapper data) {
        public record DataWrapper(@NotBlank String code) {
        }
    }

    public record RefreshRequest(DataWrapper data) {
        public record DataWrapper(@NotBlank @JsonProperty("refresh_token") String refreshToken) {
        }
    }

    public record LogoutRequest(DataWrapper data) {
        public record DataWrapper(@NotBlank @JsonProperty("refresh_token") String refreshToken) {
        }
    }

    @PostMapping("/oauth/github/exchange")
    @Operation(
            operationId = "exchangeGithubOAuthCode",
            summary = "GitHub OAuth code 교환",
            description = "GitHub OAuth authorization code를 새 access/refresh token으로 교환한다.\n토큰은 JSON 응답 body 필드로만 반환되며 cookies에 중복되지 않는다.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "Access 및 refresh token 발급 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "누락, 만료, 무효, 또는 이미 회전된 자격 증명.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> exchangeGithubCode(
            @Valid @RequestBody GithubExchangeRequest request) {
        Map<String, Object> tokens = authService.exchangeGithubCode(request.data().code());
        return ResponseEntity.ok(DataMetaEnvelope.of(tokens));
    }

    @PostMapping("/refresh")
    @Operation(
            operationId = "rotateRefreshToken",
            summary = "Refresh token 회전",
            description = "성공 호출마다 refresh token을 회전하고, 이전에 제시된 token을 즉시 무효화한다.\n이미 무효화된 refresh token을 재사용하면 401 RFC9457 problem details로 실패한다.",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "새 access 및 refresh token 발급 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "누락, 만료, 무효, 또는 이미 회전된 자격 증명.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        Map<String, Object> tokens = authService.rotateRefreshToken(request.data().refreshToken());
        return ResponseEntity.ok(DataMetaEnvelope.of(tokens));
    }

    @PostMapping("/logout")
    @Operation(
            operationId = "logoutCurrentDevice",
            summary = "현재 디바이스 로그아웃",
            description = "제공된 refresh token이 나타내는 현재 디바이스 세션만 폐기한다.\nv1 엔드포인트는 전체 디바이스 또는 계정 단위 로그아웃을 수행하지 않는다.",
            security = {}
    )
    @ApiResponse(responseCode = "204", description = "세션 폐기 성공.")
    @ApiResponse(responseCode = "400", description = "형식이 잘못된 요청 payload 또는 미지원 body 형태.")
    @ApiResponse(responseCode = "401", description = "누락, 만료, 무효, 또는 이미 회전된 자격 증명.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.data().refreshToken());
        return ResponseEntity.noContent().build();
    }
}
