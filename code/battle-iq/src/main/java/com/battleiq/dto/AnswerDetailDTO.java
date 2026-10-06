package com.battleiq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerDetailDTO {
    private String questionText;
    private String userAns;
    private String correctAnswer;
    private Boolean correct;
    private Integer scoreEarned;
}