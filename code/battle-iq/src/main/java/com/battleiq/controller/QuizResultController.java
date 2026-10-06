package com.battleiq.controller;

import com.battleiq.dto.QuizResultDTO;
import com.battleiq.dto.QuizSessionResponseDTO;
import com.battleiq.service.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;

    @GetMapping("/quiz-sessions/{id}/result")
    public ResponseEntity<QuizResultDTO> getResult(@PathVariable("id") Long sessionId) {
        return ResponseEntity.ok(quizResultService.getResult(sessionId));
    }

    @GetMapping("/users/{userId}/quiz-sessions")
    public ResponseEntity<List<QuizSessionResponseDTO>> getHistory(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(quizResultService.getHistory(userId));
    }
}