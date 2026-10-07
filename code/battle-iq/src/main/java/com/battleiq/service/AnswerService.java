package com.battleiq.service;

import com.battleiq.dto.AnswerRequestDTO;
import com.battleiq.dto.AnswerResultDTO;

public interface AnswerService {

    /**
     * รับคำตอบ 1 ข้อ ตรวจถูกผิด คิดคะแนน แล้วบันทึกลง QuizDetail
     */
    AnswerResultDTO submitAnswer(Long sessionId, AnswerRequestDTO request);
}
