package com.onlogn.onlogn.controller;

import com.onlogn.onlogn.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class OAuthPageController {

    private final AuthService authService;
    private final String clientId;

    public OAuthPageController(
            AuthService authService,
            @Value("${app.github.client-id:}") String clientId
    ) {
        this.authService = authService;
        this.clientId = clientId;
    }

    @GetMapping("/oauth2/authorization/github")
    public String redirectToGithub() {
        String redirectUri = URLEncoder.encode(CALLBACK_URI, StandardCharsets.UTF_8);
        String scope = URLEncoder.encode("read:user user:email", StandardCharsets.UTF_8);
        return "redirect:https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;
    }

    private static final String CALLBACK_URI = "http://localhost:3000/login/oauth2/callback";

    @GetMapping("/login/oauth2/callback")
    public String handleCallback(@RequestParam("code") String code, Model model) {
        Map<String, Object> tokens = authService.exchangeGithubCode(code, CALLBACK_URI);
        model.addAttribute("accessToken", tokens.get("access_token"));
        model.addAttribute("refreshToken", tokens.get("refresh_token"));
        model.addAttribute("expiresIn", tokens.get("expires_in"));
        return "oauth-callback";
    }
}
