package com.battleiq.service;

import java.util.List;

import com.battleiq.dto.response.QuizResultDTO;
import com.battleiq.dto.response.QuizSessionResponseDTO;

public interface QuizResultService {

    QuizResultDTO getResult(Long sessionId);

    List<QuizSessionResponseDTO> getHistory(Long userId);
}
