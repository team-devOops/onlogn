package com.onlogn.onlogn.controller;

import com.onlogn.onlogn.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProfilePageController {

    private final UserRepository userRepository;

    public ProfilePageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/@{slug}")
    public String profilePage(@PathVariable String slug, Model model) {
        if (userRepository.findByProfileSlug(slug).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("slug", slug);
        return "profile";
    }
}
