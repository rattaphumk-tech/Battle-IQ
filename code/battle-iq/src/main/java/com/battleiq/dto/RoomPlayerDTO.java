package com.battleiq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomPlayerDTO {
    private Long userId;
    private String username;
    private String fullName;
    private Boolean host;
    private Long sessionId;
    private Integer score;
    private Boolean finished;
}
