package com.battleiq.controller.api;

import com.battleiq.dto.response.QuestionDTO;
import com.battleiq.dto.request.QuizSessionRequestDTO;
import com.battleiq.dto.response.QuizSessionResponseDTO;
import com.battleiq.service.QuizSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionDTO>> getSessionQuestions(@PathVariable("id") Long sessionId) {
        return ResponseEntity.ok(quizSessionService.getSessionQuestions(sessionId));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<QuizSessionResponseDTO> completeQuizSession(@PathVariable("id") Long sessionId) {
        QuizSessionResponseDTO response = quizSessionService.completeSession(sessionId);
        return ResponseEntity.ok(response);
    }
}