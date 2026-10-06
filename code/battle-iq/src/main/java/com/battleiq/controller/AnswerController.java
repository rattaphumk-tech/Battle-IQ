package com.battleiq.controller;

import com.battleiq.dto.AnswerRequestDTO;
import com.battleiq.dto.AnswerResultDTO;
import com.battleiq.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz-sessions")
@RequiredArgsConstructor
@Tag(name = "Answers", description = "ส่งคำตอบระหว่างเล่นเกม")
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "ส่งคำตอบ 1 ข้อของรอบที่กำลังเล่น")
    @PostMapping("/{sessionId}/answers")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@PathVariable("sessionId") Long sessionId,
                                                        @Valid @RequestBody AnswerRequestDTO request) {
        return ResponseEntity.ok(answerService.submitAnswer(sessionId, request));
    }
}
