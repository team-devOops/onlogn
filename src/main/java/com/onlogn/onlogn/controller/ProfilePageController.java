package com.onlogn.onlogn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfilePageController {

    @GetMapping("/@{slug}")
    public String profilePage(@PathVariable String slug, Model model) {
        model.addAttribute("slug", slug);
        return "profile";
    }
}
