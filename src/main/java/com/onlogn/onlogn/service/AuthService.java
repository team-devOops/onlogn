package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.RefreshTokenEntity;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.RefreshTokenRepository;
import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 1_209_600L;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final GithubOAuthClient githubOAuthClient;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtProvider jwtProvider,
                       GithubOAuthClient githubOAuthClient) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.githubOAuthClient = githubOAuthClient;
    }

    @Transactional
    public Map<String, Object> exchangeGithubCode(String code) {
        return exchangeGithubCode(code, null);
    }

    @Transactional
    public Map<String, Object> exchangeGithubCode(String code, String redirectUri) {
        UserEntity user;

        if (githubOAuthClient.isConfigured()) {
            String ghAccessToken = githubOAuthClient.exchangeCodeForToken(code, redirectUri != null ? redirectUri : "");
            GithubOAuthClient.GithubUser ghUser = githubOAuthClient.fetchUser(ghAccessToken);
            user = findOrCreateFromGithub(ghUser);
        } else {
            log.warn("GitHub OAuth not configured — falling back to mock mode (code used as githubId)");
            user = userRepository.findByGithubId(code)
                    .orElseGet(() -> {
                        UserEntity newUser = new UserEntity();
                        newUser.setGithubId(code);
                        newUser.setProfileSlug("user-" + code);
                        newUser.setDisplayName("GitHub User " + code);
                        return userRepository.save(newUser);
                    });
        }

        return buildTokenResponse(user.getId());
    }

    private UserEntity findOrCreateFromGithub(GithubOAuthClient.GithubUser ghUser) {
        return userRepository.findByGithubId(ghUser.id())
                .map(existing -> {
                    if (ghUser.name() != null) existing.setDisplayName(ghUser.name());
                    if (ghUser.avatarUrl() != null) existing.setAvatarUrl(ghUser.avatarUrl());
                    if (ghUser.email() != null) existing.setEmail(ghUser.email());
                    if (ghUser.bio() != null) existing.setBio(ghUser.bio());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setGithubId(ghUser.id());
                    newUser.setProfileSlug(ghUser.login());
                    newUser.setDisplayName(ghUser.name() != null ? ghUser.name() : ghUser.login());
                    newUser.setEmail(ghUser.email());
                    newUser.setAvatarUrl(ghUser.avatarUrl());
                    newUser.setBio(ghUser.bio());
                    return userRepository.save(newUser);
                });
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
