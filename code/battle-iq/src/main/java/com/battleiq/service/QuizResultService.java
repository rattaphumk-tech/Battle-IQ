package com.battleiq.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.enums.SessionStatus;
import com.battleiq.dto.AnswerDetailDTO;
import com.battleiq.dto.QuizResultDTO;
import com.battleiq.dto.QuizSessionResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.QuizDetailRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizDetailRepository quizDetailRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public QuizResultDTO getResult(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
        if (!SessionStatus.COMPLETED.name().equals(session.getStatus())) {
            throw new ConflictException("Session " + sessionId + " is not completed yet");
        }

        List<QuizDetail> details = quizDetailRepository.findByQuizSessionId(sessionId);
        List<AnswerDetailDTO> answers = details.stream()
                .map(this::toAnswerDetail)
                .toList();
        int correctCount = (int) details.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsCorrect()))
                .count();

        return QuizResultDTO.builder()
                .sessionId(session.getId())
                .categoryName(session.getCategory().getName())
                .totalQuestions(session.getTotalQuestions())
                .correctCount(correctCount)
                .totalScore(session.getTotalScore())
                .completedAt(session.getCompletedAt())
                .answers(answers)
                .build();
    }

    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return quizSessionRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(QuizSession::getCreatedAt).reversed())
                .map(this::toSessionResponse)
                .toList();
    }

    private AnswerDetailDTO toAnswerDetail(QuizDetail detail) {
        return AnswerDetailDTO.builder()
                .questionText(detail.getQuestion().getQuestionText())
                .userAns(detail.getUserAns())
                .correctAnswer(detail.getQuestion().getCorrectAnswer())
                .correct(detail.getIsCorrect())
                .scoreEarned(detail.getScoreEarned())
                .build();
    }

    private QuizSessionResponseDTO toSessionResponse(QuizSession session) {
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
}