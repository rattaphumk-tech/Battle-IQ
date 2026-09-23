package com.battleiq.controller;

import com.battleiq.domain.entity.UserProfile;
import com.battleiq.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfile> getProfileByUserId(@PathVariable("userId") Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
