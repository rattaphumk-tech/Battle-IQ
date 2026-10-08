package com.battleiq.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AnswerRequestDTO {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer is required")
    @Pattern(regexp = "[ABCD]", message = "Answer must be A, B, C or D")
    private String answer;

    @NotNull(message = "Time taken is required")
    @Min(value = 0, message = "Time taken cannot be negative")
    private Integer timeTakenSeconds;
}