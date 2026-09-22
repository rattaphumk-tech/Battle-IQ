package com.battleiq.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizSessionRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}