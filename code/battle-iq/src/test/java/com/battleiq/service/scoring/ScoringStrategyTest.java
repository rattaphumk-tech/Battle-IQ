package com.battleiq.service.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.battleiq.domain.entity.Question;

class ScoringStrategyTest {

    private final BasicScoringStrategy basic = new BasicScoringStrategy();
    private final TimeBonusScoringStrategy timeBonus = new TimeBonusScoringStrategy();
    private final StreakScoringStrategy streak = new StreakScoringStrategy(timeBonus);
    private final ScoringStrategySelector selector = new ScoringStrategySelector(basic, timeBonus, streak);

    private Question questionWithLimit(Integer limit) {
        return Question.builder().timeLimitSeconds(limit).build();
    }

    @Test
    void timeBonusGivesFullBonusWhenAnsweredInstantly() {
        assertEquals(20, timeBonus.calculate(questionWithLimit(20), 0, 0));
    }

    @Test
    void timeBonusGivesBaseScoreWhenTimeRunsOut() {
        assertEquals(10, timeBonus.calculate(questionWithLimit(20), 25, 0));
    }

    @Test
    void streakAddsTenPercentPerStreakUpToFive() {
        assertEquals(26, streak.calculate(questionWithLimit(20), 0, 3));
        assertEquals(30, streak.calculate(questionWithLimit(20), 0, 9));
    }

    @Test
    void selectorPicksStrategyFromStreakAndTimeLimit() {
        assertSame(basic, selector.select(questionWithLimit(null), 5));
        assertSame(timeBonus, selector.select(questionWithLimit(15), 1));
        assertSame(streak, selector.select(questionWithLimit(15), 2));
    }
}