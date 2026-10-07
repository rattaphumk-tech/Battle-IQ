package com.battleiq.service;

import java.util.List;

import com.battleiq.dto.QuizResultDTO;
import com.battleiq.dto.QuizSessionResponseDTO;

public interface QuizResultService {

    QuizResultDTO getResult(Long sessionId);

    List<QuizSessionResponseDTO> getHistory(Long userId);
}
