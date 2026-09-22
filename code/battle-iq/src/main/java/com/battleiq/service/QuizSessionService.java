package com.battleiq.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.User;
import com.battleiq.dto.QuizSessionRequestDTO;
import com.battleiq.dto.QuizSessionResponseDTO;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionFactory questionFactory;

    /**
     * เริ่มต้นรอบการเล่นเกมใหม่ (Start Quiz Session)
     */
    @Transactional
    public QuizSessionResponseDTO startSession(QuizSessionRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // ดึงชุดคำถามสุ่ม 5 ข้อผ่าน Factory
        List<Question> questions = questionFactory.createQuizQuestions(category.getId(), 5);

        QuizSession session = QuizSession.builder()
                .user(user)
                .category(category)
                .totalQuestions(questions.size())
                .totalScore(0)
                .status("IN_PROGRESS")
                .build();

        // สร้าง QuizDetail สำหรับแต่ละข้อสอบ
        for (Question q : questions) {
            QuizDetail detail = QuizDetail.builder()
                    .quizSession(session)
                    .question(q)
                    .isCorrect(false)
                    .timeTakenSeconds(0)
                    .scoreEarned(0)
                    .build();
            session.getDetails().add(detail);
        }

        QuizSession savedSession = quizSessionRepository.save(session);

        return QuizSessionResponseDTO.builder()
                .sessionId(savedSession.getId())
                .userId(user.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .totalQuestions(savedSession.getTotalQuestions())
                .totalScore(savedSession.getTotalScore())
                .status(savedSession.getStatus())
                .createdAt(savedSession.getCreatedAt())
                .build();
    }

    /**
     * จบรอบการเล่นเกมและสรุปคะแนน (Complete Quiz Session)
     */
    @Transactional
    public QuizSessionResponseDTO completeSession(Long sessionId, Integer finalScore) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setStatus("COMPLETED");
        session.setTotalScore(finalScore);
        session.setCompletedAt(LocalDateTime.now());

        QuizSession updatedSession = quizSessionRepository.save(session);

        return QuizSessionResponseDTO.builder()
                .sessionId(updatedSession.getId())
                .userId(updatedSession.getUser().getId())
                .categoryId(updatedSession.getCategory().getId())
                .categoryName(updatedSession.getCategory().getName())
                .totalQuestions(updatedSession.getTotalQuestions())
                .totalScore(updatedSession.getTotalScore())
                .status(updatedSession.getStatus())
                .createdAt(updatedSession.getCreatedAt())
                .build();
    }
}