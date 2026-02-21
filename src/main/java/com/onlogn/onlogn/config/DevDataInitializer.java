package com.onlogn.onlogn.config;

import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.security.JwtProvider;
import com.onlogn.onlogn.service.GithubOAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final GithubOAuthClient githubOAuthClient;

    public DevDataInitializer(UserRepository userRepository,
                              JwtProvider jwtProvider,
                              GithubOAuthClient githubOAuthClient) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.githubOAuthClient = githubOAuthClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (githubOAuthClient.isConfigured()) {
            log.info("GitHub OAuth configured — skipping dev token generation (use GitHub login)");
            return;
        }

        log.info("=".repeat(60));
        log.info("  DEV TEST ACCOUNTS — 토큰 자동 발급 (mock mode)");
        log.info("=".repeat(60));

        issueTokenFor("gh_alex_001", "알렉스 리베라 (arivera_dev)");
        issueTokenFor("gh_jpark_002", "박지현 (jpark_dev)");
        issueTokenFor("gh_mkim_003", "김민수 (mkim_design)");

        log.info("=".repeat(60));
    }

    private void issueTokenFor(String githubId, String label) {
        userRepository.findByGithubId(githubId).ifPresent(user -> {
            String accessToken = jwtProvider.generateAccessToken(user.getId());
            log.info("  {} [{}]", label, user.getId());
            log.info("    access_token  = {}", accessToken);
            log.info("");
        });
    }
}
