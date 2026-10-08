package com.battleiq.service.impl;

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
import com.battleiq.dto.response.QuestionDTO;
import com.battleiq.dto.request.QuizSessionRequestDTO;
import com.battleiq.dto.response.QuizSessionResponseDTO;
import com.battleiq.event.GameCompletedEvent;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.mapper.QuestionMapper;
import com.battleiq.mapper.QuizSessionMapper;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;
import com.battleiq.service.QuestionFactory;
import com.battleiq.service.QuizSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionFactory questionFactory;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * เริ่มต้นรอบการเล่นเกมใหม่ (Start Quiz Session)
     */
    @Override
    @Transactional
    public QuizSessionResponseDTO startSession(QuizSessionRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        // ดึงชุดคำถามสุ่มผ่าน Factory
        List<Question> questions = questionFactory.createQuizQuestions(category.getId(), QUESTIONS_PER_SESSION);

        return QuizSessionMapper.toResponse(createSession(user, category, questions));
    }

    @Override
    @Transactional
    public QuizSession createSession(User user, Category category, List<Question> questions) {
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

        return quizSessionRepository.save(session);
    }

    /**
     * ดึงคำถามทั้งหมดของรอบที่กำลังเล่น (ไม่ส่งเฉลย)
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getSessionQuestions(Long sessionId) {
        QuizSession session = findSession(sessionId);
        return session.getDetails().stream()
                .map(detail -> QuestionMapper.toDTO(detail.getQuestion()))
                .toList();
    }

    /**
     * จบรอบการเล่นเกมและสรุปคะแนนจากคำตอบที่บันทึกไว้ (Complete Quiz Session)
     */
    @Override
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

        return QuizSessionMapper.toResponse(updatedSession);
    }

    private QuizSession findSession(Long sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
    }
}
