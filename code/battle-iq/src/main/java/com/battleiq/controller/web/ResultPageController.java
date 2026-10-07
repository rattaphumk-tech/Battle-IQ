package com.battleiq.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResultPageController {

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @GetMapping("/history")
    public String history() {
        return "history";
    }
}