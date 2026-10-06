package com.battleiq.service.scoring;

import com.battleiq.domain.entity.Question;

public interface ScoringStrategy {

    /**
     * คำนวณคะแนนของข้อที่ตอบถูก
     */
    int calculate(Question question, int timeTakenSeconds, int currentStreak);
}