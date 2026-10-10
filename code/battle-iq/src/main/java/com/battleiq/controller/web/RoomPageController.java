package com.battleiq.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomPageController {

    @GetMapping("/rooms")
    public String rooms() {
        return "rooms";
    }

    @GetMapping("/room")
    public String room() {
        return "room";
    }
}
