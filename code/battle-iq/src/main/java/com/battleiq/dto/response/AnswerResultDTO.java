package com.battleiq.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerResultDTO {
    private Long questionId;
    private Boolean correct;
    private String correctAnswer;
    private Integer scoreEarned;
}