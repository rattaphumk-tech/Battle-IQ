package com.battleiq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDetailDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer timeLimitSeconds;
}