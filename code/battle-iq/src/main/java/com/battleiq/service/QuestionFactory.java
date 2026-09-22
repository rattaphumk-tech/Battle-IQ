package com.battleiq.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.battleiq.domain.entity.Question;
import com.battleiq.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuestionFactory {

    private final QuestionRepository questionRepository;

    public List<Question> createQuizQuestions(Long categoryId, int count) {
        // ใช้ PageRequest.of(0, count) เพื่อจำกัดจำนวนข้อแทน LIMIT ใน SQL Direct
        List<Question> randomQuestions = questionRepository.findRandomQuestionsByCategoryId(categoryId, PageRequest.of(0, count));

        if (randomQuestions.isEmpty()) {
            throw new RuntimeException("No questions available for category ID: " + categoryId);
        }

        return randomQuestions;
    }
}