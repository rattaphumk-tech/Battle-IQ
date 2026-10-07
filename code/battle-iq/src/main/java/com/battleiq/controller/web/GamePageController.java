package com.battleiq.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamePageController {

    @GetMapping("/play")
    public String play() {
        return "play";
    }
}