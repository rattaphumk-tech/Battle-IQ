package com.battleiq.service.command;

import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.dto.AnswerResultDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.service.scoring.ScoringStrategy;
import com.battleiq.service.scoring.ScoringStrategySelector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubmitAnswerCommand implements Command<AnswerResultDTO> {

    private final QuizDetail detail;
    private final String answer;
    private final int timeTakenSeconds;
    private final int currentStreak;
    private final ScoringStrategySelector strategySelector;

    @Override
    public AnswerResultDTO execute() {
        if (detail.getUserAns() != null) {
            throw new ConflictException("Question " + detail.getQuestion().getId() + " is already answered");
        }

        Question question = detail.getQuestion();
        boolean correct = question.getCorrectAnswer().equalsIgnoreCase(answer);
        int score = 0;
        if (correct) {
            ScoringStrategy strategy = strategySelector.select(question, currentStreak);
            score = strategy.calculate(question, timeTakenSeconds, currentStreak);
        }

        detail.setUserAns(answer);
        detail.setIsCorrect(correct);
        detail.setTimeTakenSeconds(timeTakenSeconds);
        detail.setScoreEarned(score);

        return AnswerResultDTO.builder()
                .questionId(question.getId())
                .correct(correct)
                .correctAnswer(question.getCorrectAnswer())
                .scoreEarned(score)
                .build();
    }
}