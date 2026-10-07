package com.battleiq.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.battleiq.dto.QuestionDTO;
import com.battleiq.dto.QuestionDetailDTO;
import com.battleiq.dto.QuestionRequestDTO;

public interface QuestionService {

    List<QuestionDTO> getQuestionsByCategory(Long categoryId);

    Page<QuestionDetailDTO> getQuestions(Pageable pageable);

    QuestionDetailDTO createQuestion(QuestionRequestDTO request);

    QuestionDetailDTO updateQuestion(Long id, QuestionRequestDTO request);

    void deleteQuestion(Long id);
}
