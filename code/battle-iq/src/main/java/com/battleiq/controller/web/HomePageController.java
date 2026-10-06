package com.battleiq.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin/questions")
    public String manageQuestions() {
        return "admin-questions";
    }
}