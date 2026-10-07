package com.battleiq.service;

import org.springframework.data.domain.Page;

import com.battleiq.dto.LeaderboardEntryDTO;

public interface LeaderboardService {

    Page<LeaderboardEntryDTO> getLeaderboard(int page, int size);
}
