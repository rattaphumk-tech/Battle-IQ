package com.battleiq.controller;

import com.battleiq.domain.entity.Question;
import com.battleiq.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable("categoryId") Long categoryId) {
        List<Question> questions = questionRepository.findByCategoryId(categoryId);
        return ResponseEntity.ok(questions);
    }
}
