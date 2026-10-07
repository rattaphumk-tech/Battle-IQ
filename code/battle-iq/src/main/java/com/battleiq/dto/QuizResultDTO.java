package com.battleiq.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizResultDTO {
    private Long sessionId;
    private String categoryName;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer totalScore;
    private LocalDateTime completedAt;
    private List<AnswerDetailDTO> answers;
}