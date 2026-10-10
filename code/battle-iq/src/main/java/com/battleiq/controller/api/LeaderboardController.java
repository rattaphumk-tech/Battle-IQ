package com.battleiq.controller.api;

import com.battleiq.dto.response.LeaderboardEntryDTO;
import com.battleiq.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<Page<LeaderboardEntryDTO>> getLeaderboard(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(page, size));
    }
}