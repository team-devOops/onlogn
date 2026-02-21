package com.onlogn.onlogn.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlogn.onlogn.common.dto.DataMetaEnvelope;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
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
            String timezone,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("updated_at") String updatedAt
    ) {
    }

    @GetMapping("/me")
    public ResponseEntity<DataMetaEnvelope<UserResponse>> me() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userService.getCurrentUser(userId);

        UserResponse response = new UserResponse(
                user.getId().toString(),
                user.getProfileSlug(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getTimezone(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );

        return ResponseEntity.ok(DataMetaEnvelope.of(response));
    }
}
