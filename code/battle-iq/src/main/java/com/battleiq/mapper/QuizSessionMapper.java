package com.battleiq.mapper;

import java.util.List;

import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.dto.AnswerDetailDTO;
import com.battleiq.dto.QuizResultDTO;
import com.battleiq.dto.QuizSessionResponseDTO;

public final class QuizSessionMapper {

    private QuizSessionMapper() {
    }

    public static QuizSessionResponseDTO toResponse(QuizSession session) {
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

    public static AnswerDetailDTO toAnswerDetail(QuizDetail detail) {
        return AnswerDetailDTO.builder()
                .questionText(detail.getQuestion().getQuestionText())
                .userAns(detail.getUserAns())
                .correctAnswer(detail.getQuestion().getCorrectAnswer())
                .correct(detail.getIsCorrect())
                .scoreEarned(detail.getScoreEarned())
                .build();
    }

    public static QuizResultDTO toResult(QuizSession session, List<QuizDetail> details) {
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
                .answers(details.stream().map(QuizSessionMapper::toAnswerDetail).toList())
                .build();
    }
}
