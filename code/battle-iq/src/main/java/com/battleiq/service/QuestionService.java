package com.battleiq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Question;
import com.battleiq.dto.QuestionDTO;
import com.battleiq.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId).stream()
                .map(this::toDTO)
                .toList();
    }

    // ไม่ส่ง correctAnswer ออกไปให้ผู้เล่น
    private QuestionDTO toDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .categoryId(question.getCategory().getId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .build();
    }
}
