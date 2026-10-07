package com.battleiq.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryPageController {

    @GetMapping("/categories")
    public String categories() {
        return "categories";
    }

    @GetMapping("/leaderboard")
    public String leaderboard() {
        return "leaderboard";
    }

    @GetMapping("/admin/categories")
    public String manageCategories() {
        return "admin-categories";
    }
}