package com.battleiq.mapper;

import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.response.LeaderboardEntryDTO;

public final class LeaderboardMapper {

    private LeaderboardMapper() {
    }

    public static LeaderboardEntryDTO toEntry(UserProfile profile, int rank) {
        return LeaderboardEntryDTO.builder()
                .rank(rank)
                .userId(profile.getId())
                .username(profile.getUser().getUsername())
                .fullName(profile.getFullName())
                .totalScore(profile.getTotalScore())
                .level(profile.getLevel())
                .build();
    }
}
