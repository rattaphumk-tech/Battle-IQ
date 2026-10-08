package com.battleiq.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionRequestDTO {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Question text is required")
    @Size(max = 1000, message = "Question text must be at most 1000 characters")
    private String questionText;

    @NotBlank(message = "Option A is required")
    @Size(max = 255, message = "Option A must be at most 255 characters")
    private String optionA;

    @NotBlank(message = "Option B is required")
    @Size(max = 255, message = "Option B must be at most 255 characters")
    private String optionB;

    @NotBlank(message = "Option C is required")
    @Size(max = 255, message = "Option C must be at most 255 characters")
    private String optionC;

    @NotBlank(message = "Option D is required")
    @Size(max = 255, message = "Option D must be at most 255 characters")
    private String optionD;

    @NotBlank(message = "Correct answer is required")
    @Pattern(regexp = "[ABCD]", message = "Correct answer must be A, B, C or D")
    private String correctAnswer;

    @Min(value = 5, message = "Time limit must be at least 5 seconds")
    @Max(value = 120, message = "Time limit must be at most 120 seconds")
    private Integer timeLimitSeconds;
}
