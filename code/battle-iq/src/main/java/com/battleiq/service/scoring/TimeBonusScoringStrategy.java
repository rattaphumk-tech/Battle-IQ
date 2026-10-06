package com.battleiq.service.scoring;

import org.springframework.stereotype.Component;

import com.battleiq.domain.entity.Question;

@Component
public class TimeBonusScoringStrategy implements ScoringStrategy {

    private static final int MAX_BONUS = 10;

    @Override
    public int calculate(Question question, int timeTakenSeconds, int currentStreak) {
        int limit = question.getTimeLimitSeconds();
        int remaining = Math.max(limit - timeTakenSeconds, 0);
        // ตอบเร็วได้โบนัสมาก ตอบหมดเวลาพอดีได้แค่คะแนนพื้นฐาน
        return BasicScoringStrategy.BASE_SCORE + MAX_BONUS * remaining / limit;
    }
}