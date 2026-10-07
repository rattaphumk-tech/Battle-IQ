package com.battleiq.service;

import java.util.List;

import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.User;
import com.battleiq.dto.QuestionDTO;
import com.battleiq.dto.QuizSessionRequestDTO;
import com.battleiq.dto.QuizSessionResponseDTO;

public interface QuizSessionService {

    int QUESTIONS_PER_SESSION = 5;

    QuizSessionResponseDTO startSession(QuizSessionRequestDTO request);

    /**
     * สร้าง session จากชุดคำถามที่กำหนด ใช้ทั้งเล่นคนเดียวและเล่นในห้องที่ทุกคนได้คำถามชุดเดียวกัน
     */
    QuizSession createSession(User user, Category category, List<Question> questions);

    List<QuestionDTO> getSessionQuestions(Long sessionId);

    QuizSessionResponseDTO completeSession(Long sessionId);
}
