package com.battleiq.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.dto.QuestionDTO;
import com.battleiq.dto.QuestionDetailDTO;
import com.battleiq.dto.QuestionRequestDTO;
import com.battleiq.exception.CategoryNotFoundException;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.QuestionNotFoundException;
import com.battleiq.mapper.QuestionMapper;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.QuestionRepository;
import com.battleiq.service.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final int DEFAULT_TIME_LIMIT_SECONDS = 15;

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId).stream()
                .map(QuestionMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDetailDTO> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionMapper::toDetailDTO);
    }

    @Override
    @Transactional
    public QuestionDetailDTO createQuestion(QuestionRequestDTO request) {
        Question question = new Question();
        applyRequest(question, request);
        return QuestionMapper.toDetailDTO(questionRepository.save(question));
    }

    @Override
    @Transactional
    public QuestionDetailDTO updateQuestion(Long id, QuestionRequestDTO request) {
        Question question = findQuestion(id);
        applyRequest(question, request);
        return QuestionMapper.toDetailDTO(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question question = findQuestion(id);
        try {
            questionRepository.delete(question);
            questionRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            // คำถามที่เคยถูกใช้ในรอบการเล่นแล้วจะลบไม่ได้
            throw new ConflictException("Question " + id + " is used in quiz sessions and cannot be deleted");
        }
    }

    private Question findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with ID: " + id));
    }

    private void applyRequest(Question question, QuestionRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + request.getCategoryId()));

        question.setCategory(category);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setTimeLimitSeconds(
                request.getTimeLimitSeconds() != null ? request.getTimeLimitSeconds() : DEFAULT_TIME_LIMIT_SECONDS);
    }
}
