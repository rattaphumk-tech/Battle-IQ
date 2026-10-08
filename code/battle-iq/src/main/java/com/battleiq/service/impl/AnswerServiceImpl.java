package com.battleiq.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.AnswerRequestDTO;
import com.battleiq.dto.AnswerResultDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.QuizDetailRepository;
import com.battleiq.repository.UserProfileRepository;
import com.battleiq.service.AnswerService;
import com.battleiq.service.command.Command;
import com.battleiq.service.command.SubmitAnswerCommand;
import com.battleiq.service.scoring.ScoringStrategySelector;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final QuizDetailRepository quizDetailRepository;
    private final UserProfileRepository userProfileRepository;
    private final ScoringStrategySelector strategySelector;

    @Override
    @Transactional
    public AnswerResultDTO submitAnswer(Long sessionId, AnswerRequestDTO request) {
        QuizDetail detail = quizDetailRepository
                .findByQuizSessionIdAndQuestionId(sessionId, request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question " + request.getQuestionId() + " is not in session " + sessionId));

        QuizSession session = detail.getQuizSession();
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new ConflictException("Session " + sessionId + " is already " + session.getStatus());
        }

        int currentStreak = userProfileRepository.findByUserId(session.getUser().getId())
                .map(UserProfile::getCurrentStreak)
                .orElse(0);

        Command<AnswerResultDTO> command = new SubmitAnswerCommand(
                detail, request.getAnswer(), request.getTimeTakenSeconds(), currentStreak, strategySelector);
        AnswerResultDTO result = command.execute();

        quizDetailRepository.save(detail);
        return result;
    }
}
