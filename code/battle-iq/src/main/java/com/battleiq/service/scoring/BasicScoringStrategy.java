package com.battleiq.service.scoring;

import org.springframework.stereotype.Component;

import com.battleiq.domain.entity.Question;

@Component
public class BasicScoringStrategy implements ScoringStrategy {

    public static final int BASE_SCORE = 10;

    @Override
    public int calculate(Question question, int timeTakenSeconds, int currentStreak) {
        return BASE_SCORE;
    }
}