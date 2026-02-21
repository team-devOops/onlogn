package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.service.AuthService;
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
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> exchangeGithubCode(
            @Valid @RequestBody GithubExchangeRequest request) {
        Map<String, Object> tokens = authService.exchangeGithubCode(request.data().code());
        return ResponseEntity.ok(DataMetaEnvelope.of(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataMetaEnvelope<Map<String, Object>>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        Map<String, Object> tokens = authService.rotateRefreshToken(request.data().refreshToken());
        return ResponseEntity.ok(DataMetaEnvelope.of(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.data().refreshToken());
        return ResponseEntity.noContent().build();
    }
}
