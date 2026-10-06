package com.battleiq.service.scoring;

import org.springframework.stereotype.Component;

import com.battleiq.domain.entity.Question;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScoringStrategySelector {

    private static final int STREAK_THRESHOLD = 2;

    private final BasicScoringStrategy basicScoringStrategy;
    private final TimeBonusScoringStrategy timeBonusScoringStrategy;
    private final StreakScoringStrategy streakScoringStrategy;

    public ScoringStrategy select(Question question, int currentStreak) {
        Integer limit = question.getTimeLimitSeconds();
        if (limit == null || limit <= 0) {
            return basicScoringStrategy;
        }
        if (currentStreak >= STREAK_THRESHOLD) {
            return streakScoringStrategy;
        }
        return timeBonusScoringStrategy;
    }
}