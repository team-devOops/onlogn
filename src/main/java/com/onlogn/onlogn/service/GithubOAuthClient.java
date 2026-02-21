package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GithubOAuthClient {

    private static final Logger log = LoggerFactory.getLogger(GithubOAuthClient.class);

    private final String clientId;
    private final String clientSecret;
    private final RestClient restClient;

    public GithubOAuthClient(
            @Value("${app.github.client-id:}") String clientId,
            @Value("${app.github.client-secret:}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = RestClient.create();
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public String exchangeCodeForToken(String code) {
        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || response.containsKey("error")) {
            String error = response != null ? String.valueOf(response.get("error_description")) : "No response";
            log.warn("GitHub token exchange failed: {}", error);
            throw new ApiException(
                    ProblemType.AUTHENTICATION_REQUIRED,
                    "GitHub OAuth code is invalid or expired: " + error,
                    "/api/v1/auth/oauth/github/exchange");
        }

        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    public GithubUser fetchUser(String accessToken) {
        Map<String, Object> response = restClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("id")) {
            throw new ApiException(
                    ProblemType.AUTHENTICATION_REQUIRED,
                    "Failed to fetch GitHub user info",
                    "/api/v1/auth/oauth/github/exchange");
        }

        return new GithubUser(
                String.valueOf(response.get("id")),
                (String) response.get("login"),
                (String) response.get("name"),
                (String) response.get("email"),
                (String) response.get("avatar_url"),
                (String) response.get("bio")
        );
    }

    public record GithubUser(
            String id,
            String login,
            String name,
            String email,
            String avatarUrl,
            String bio
    ) {}
}
