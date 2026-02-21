package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public record UserResponse(
            String id,
            @JsonProperty("profile_slug") String profileSlug,
            String email,
            @JsonProperty("display_name") String displayName,
            @JsonProperty("avatar_url") String avatarUrl,
            String bio,
            String timezone,
            String visibility,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("updated_at") String updatedAt
    ) {
    }

    @GetMapping("/me")
    @Operation(
            operationId = "getCurrentUser",
            summary = "현재 인증 사용자 조회",
            description = "private 컨텍스트에서 인증 사용자의 전체 프로필 payload를 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "인증 사용자 프로필 반환 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    @ApiResponse(responseCode = "429", description = "스로틀링 정책에 의해 요청이 거부됨.")
    public ResponseEntity<DataMetaEnvelope<UserResponse>> me() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userService.getCurrentUser(userId);

        return ResponseEntity.ok(DataMetaEnvelope.of(toUserResponse(user)));
    }

    public record UpdateUserRequest(
            String bio,
            @JsonProperty("display_name") String displayName,
            String timezone,
            String visibility
    ) {
    }

    @PatchMapping("/me")
    @Operation(
            operationId = "updateCurrentUser",
            summary = "현재 인증 사용자 프로필 수정",
            description = "인증 사용자의 프로필 정보를 부분 수정한다."
    )
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공.")
    @ApiResponse(responseCode = "401", description = "RFC9457 problem details 응답.")
    public ResponseEntity<DataMetaEnvelope<UserResponse>> updateMe(@RequestBody UpdateUserRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userService.updateCurrentUser(userId, request.bio(), request.displayName(), request.timezone(), request.visibility());
        return ResponseEntity.ok(DataMetaEnvelope.of(toUserResponse(user)));
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId().toString(),
                user.getProfileSlug(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getTimezone(),
                user.getVisibility(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}
