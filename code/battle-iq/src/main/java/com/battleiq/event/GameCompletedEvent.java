package com.battleiq.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCompletedEvent {
    private final Long sessionId;
    private final Long userId;
    private final Integer totalScore;
}
