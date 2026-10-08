package com.battleiq.controller.api;

import com.battleiq.dto.request.ProfileUpdateDTO;
import com.battleiq.dto.response.UserResponseDTO;
import com.battleiq.service.UserProfileService;
import com.battleiq.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> getProfileByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> updateProfile(@PathVariable("userId") Long userId,
                                                         @Valid @RequestBody ProfileUpdateDTO request) {
        userProfileService.updateProfile(userId, request);
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
