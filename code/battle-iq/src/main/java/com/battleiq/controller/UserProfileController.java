package com.battleiq.controller;

import com.battleiq.dto.UserResponseDTO;
import com.battleiq.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> getProfileByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
