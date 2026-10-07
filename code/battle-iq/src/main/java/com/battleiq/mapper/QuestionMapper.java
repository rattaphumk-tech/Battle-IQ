package com.battleiq.mapper;

import com.battleiq.domain.entity.Question;
import com.battleiq.dto.QuestionDTO;
import com.battleiq.dto.QuestionDetailDTO;

public final class QuestionMapper {

    private QuestionMapper() {
    }

    // สำหรับผู้เล่น ไม่ส่ง correctAnswer
    public static QuestionDTO toDTO(Question question) {
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

    // สำหรับหน้าจัดการคำถาม ส่งเฉลยไปด้วย
    public static QuestionDetailDTO toDetailDTO(Question question) {
        return QuestionDetailDTO.builder()
                .id(question.getId())
                .categoryId(question.getCategory().getId())
                .categoryName(question.getCategory().getName())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctAnswer(question.getCorrectAnswer())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .build();
    }
}
