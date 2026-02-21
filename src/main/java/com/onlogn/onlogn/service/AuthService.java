package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.RefreshTokenEntity;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.RefreshTokenRepository;
import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 1_209_600L;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public Map<String, Object> exchangeGithubCode(String code) {
        UserEntity user = userRepository.findByGithubId(code)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setGithubId(code);
                    newUser.setProfileSlug("user-" + code);
                    newUser.setDisplayName("GitHub User " + code);
                    return userRepository.save(newUser);
                });

        return buildTokenResponse(user.getId());
    }

    @Transactional
    public Map<String, Object> rotateRefreshToken(String refreshToken) {
        RefreshTokenEntity existing = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(
                        ProblemType.AUTHENTICATION_REQUIRED,
                        "Invalid refresh token",
                        "/api/v1/auth/refresh"));

        if (existing.isRevoked()) {
            throw new ApiException(
                    ProblemType.AUTHENTICATION_REQUIRED,
                    "Refresh token has been revoked",
                    "/api/v1/auth/refresh");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(
                    ProblemType.AUTHENTICATION_REQUIRED,
                    "Refresh token has expired",
                    "/api/v1/auth/refresh");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return buildTokenResponse(existing.getUserId());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private Map<String, Object> buildTokenResponse(UUID userId) {
        String accessToken = jwtProvider.generateAccessToken(userId);

        String refreshTokenValue = "rf_" + UUID.randomUUID();
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken(refreshTokenValue);
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));
        refreshTokenRepository.save(refreshTokenEntity);

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshTokenValue,
                "token_type", "Bearer",
                "expires_in", jwtProvider.getAccessTokenExpirationSeconds(),
                "refresh_expires_in", REFRESH_TOKEN_EXPIRATION_SECONDS
        );
    }
}
