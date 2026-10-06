package com.battleiq.service.scoring;

import org.springframework.stereotype.Component;

import com.battleiq.domain.entity.Question;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StreakScoringStrategy implements ScoringStrategy {

    private static final int MAX_STREAK = 5;
    private static final int BONUS_PERCENT_PER_STREAK = 10;

    private final TimeBonusScoringStrategy timeBonusScoringStrategy;

    @Override
    public int calculate(Question question, int timeTakenSeconds, int currentStreak) {
        int score = timeBonusScoringStrategy.calculate(question, timeTakenSeconds, currentStreak);
        int bonusPercent = Math.min(currentStreak, MAX_STREAK) * BONUS_PERCENT_PER_STREAK;
        return score + score * bonusPercent / 100;
    }
}