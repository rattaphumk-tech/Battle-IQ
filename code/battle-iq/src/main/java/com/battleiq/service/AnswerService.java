package com.battleiq.service;

import com.battleiq.dto.request.AnswerRequestDTO;
import com.battleiq.dto.response.AnswerResultDTO;

public interface AnswerService {

    /**
     * รับคำตอบ 1 ข้อ ตรวจถูกผิด คิดคะแนน แล้วบันทึกลง QuizDetail
     */
    AnswerResultDTO submitAnswer(Long sessionId, AnswerRequestDTO request);
}
