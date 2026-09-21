package com.battleiq.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizSessionResponseDTO {
    private Long sessionId;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private Integer totalQuestions;
    private Integer totalScore;
    private String status;
    private LocalDateTime createdAt;
}