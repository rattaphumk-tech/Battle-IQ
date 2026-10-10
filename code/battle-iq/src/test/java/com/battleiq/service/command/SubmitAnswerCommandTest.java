package com.battleiq.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.dto.response.AnswerResultDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.service.scoring.ScoringStrategy;
import com.battleiq.service.scoring.ScoringStrategySelector;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerCommandTest {

    @Mock
    private ScoringStrategySelector strategySelector;
    @Mock
    private ScoringStrategy scoringStrategy;

    private Question question;
    private QuizDetail detail;

    @BeforeEach
    void setUp() {
        question = Question.builder().id(3L).correctAnswer("B").timeLimitSeconds(15).build();
        detail = QuizDetail.builder().question(question).build();
    }

    @Test
    void correctAnswerGetsScoreFromStrategy() {
        when(strategySelector.select(question, 1)).thenReturn(scoringStrategy);
        when(scoringStrategy.calculate(question, 4, 1)).thenReturn(17);

        AnswerResultDTO result = new SubmitAnswerCommand(detail, "B", 4, 1, strategySelector).execute();

        assertTrue(result.getCorrect());
        assertEquals(17, result.getScoreEarned());
        assertEquals("B", detail.getUserAns());
        assertEquals(17, detail.getScoreEarned());
    }

    @Test
    void wrongAnswerGetsZeroAndSkipsStrategy() {
        AnswerResultDTO result = new SubmitAnswerCommand(detail, "A", 4, 1, strategySelector).execute();

        assertFalse(result.getCorrect());
        assertEquals(0, result.getScoreEarned());
        assertEquals("B", result.getCorrectAnswer());
        verifyNoInteractions(strategySelector);
    }

    @Test
    void answeringSameQuestionTwiceIsRejected() {
        detail.setUserAns("A");

        assertThrows(ConflictException.class,
                () -> new SubmitAnswerCommand(detail, "B", 4, 1, strategySelector).execute());
    }
}