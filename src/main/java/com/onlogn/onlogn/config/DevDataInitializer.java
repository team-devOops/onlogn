package com.onlogn.onlogn.config;

import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.UserRepository;
import com.onlogn.onlogn.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final UserRepository userRepository;
    private final AuthService authService;

    public DevDataInitializer(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("=".repeat(60));
        log.info("  DEV TEST ACCOUNTS — 토큰 자동 발급");
        log.info("=".repeat(60));

        issueTokenFor("gh_alex_001", "알렉스 리베라 (arivera_dev)");
        issueTokenFor("gh_jpark_002", "박지현 (jpark_dev)");
        issueTokenFor("gh_mkim_003", "김민수 (mkim_design)");

        log.info("=".repeat(60));
    }

    private void issueTokenFor(String githubId, String label) {
        userRepository.findByGithubId(githubId).ifPresent(user -> {
            Map<String, Object> tokens = authService.exchangeGithubCode(githubId);
            log.info("  {} [{}]", label, user.getId());
            log.info("    access_token  = {}", tokens.get("access_token"));
            log.info("    refresh_token = {}", tokens.get("refresh_token"));
            log.info("");
        });
    }
}
