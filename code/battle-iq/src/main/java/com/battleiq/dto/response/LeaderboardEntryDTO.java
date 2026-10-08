package com.battleiq.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDTO {
    private Integer rank;
    private Long userId;
    private String username;
    private String fullName;
    private Integer totalScore;
    private Integer level;
}