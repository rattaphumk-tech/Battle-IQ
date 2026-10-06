package com.battleiq.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.User;
import com.battleiq.dto.QuestionDTO;
import com.battleiq.dto.QuizSessionRequestDTO;
import com.battleiq.dto.QuizSessionResponseDTO;
import com.battleiq.event.GameCompletedEvent;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private static final int QUESTIONS_PER_SESSION = 5;

    private final QuizSessionRepository quizSessionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionFactory questionFactory;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * เริ่มต้นรอบการเล่นเกมใหม่ (Start Quiz Session)
     */
    @Transactional
    public QuizSessionResponseDTO startSession(QuizSessionRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        // ดึงชุดคำถามสุ่มผ่าน Factory
        List<Question> questions = questionFactory.createQuizQuestions(category.getId(), QUESTIONS_PER_SESSION);

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
        return toResponse(savedSession);
    }

    /**
     * ดึงคำถามทั้งหมดของรอบที่กำลังเล่น (ไม่ส่งเฉลย)
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> getSessionQuestions(Long sessionId) {
        QuizSession session = findSession(sessionId);
        return session.getDetails().stream()
                .map(detail -> toQuestionDTO(detail.getQuestion()))
                .toList();
    }

    /**
     * จบรอบการเล่นเกมและสรุปคะแนนจากคำตอบที่บันทึกไว้ (Complete Quiz Session)
     */
    @Transactional
    public QuizSessionResponseDTO completeSession(Long sessionId) {
        QuizSession session = findSession(sessionId);

        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new ConflictException("Session " + sessionId + " is already " + session.getStatus());
        }

        int totalScore = session.getDetails().stream()
                .mapToInt(QuizDetail::getScoreEarned)
                .sum();

        session.setStatus("COMPLETED");
        session.setTotalScore(totalScore);
        session.setCompletedAt(LocalDateTime.now());

        QuizSession updatedSession = quizSessionRepository.save(session);

        eventPublisher.publishEvent(
                new GameCompletedEvent(updatedSession.getId(), updatedSession.getUser().getId(), totalScore));

        return toResponse(updatedSession);
    }

    private QuizSession findSession(Long sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
    }

    private QuizSessionResponseDTO toResponse(QuizSession session) {
        return QuizSessionResponseDTO.builder()
                .sessionId(session.getId())
                .userId(session.getUser().getId())
                .categoryId(session.getCategory().getId())
                .categoryName(session.getCategory().getName())
                .totalQuestions(session.getTotalQuestions())
                .totalScore(session.getTotalScore())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .build();
    }

    private QuestionDTO toQuestionDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .categoryId(question.getCategory().getId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .build();
    }
}