package com.battleiq.controller;

import com.battleiq.dto.QuizSessionRequestDTO;
import com.battleiq.dto.QuizSessionResponseDTO;
import com.battleiq.service.QuizSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz-sessions")
@RequiredArgsConstructor
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    @PostMapping("/start")
    public ResponseEntity<QuizSessionResponseDTO> startQuizSession(@Valid @RequestBody QuizSessionRequestDTO request) {
        QuizSessionResponseDTO response = quizSessionService.startSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/{id}/complete")
    public ResponseEntity<QuizSessionResponseDTO> completeQuizSession(
            @PathVariable("id") Long sessionId,
            @RequestParam("score") Integer finalScore) {
        QuizSessionResponseDTO response = quizSessionService.completeSession(sessionId, finalScore);
        return ResponseEntity.ok(response);
    }
}