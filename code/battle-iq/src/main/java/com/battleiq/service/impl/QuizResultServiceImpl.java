package com.battleiq.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.enums.SessionStatus;
import com.battleiq.dto.response.QuizResultDTO;
import com.battleiq.dto.response.QuizSessionResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.mapper.QuizSessionMapper;
import com.battleiq.repository.QuizDetailRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;
import com.battleiq.service.QuizResultService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizResultServiceImpl implements QuizResultService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizDetailRepository quizDetailRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public QuizResultDTO getResult(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
        if (!SessionStatus.COMPLETED.name().equals(session.getStatus())) {
            throw new ConflictException("Session " + sessionId + " is not completed yet");
        }

        List<QuizDetail> details = quizDetailRepository.findByQuizSessionId(sessionId);
        return QuizSessionMapper.toResult(session, details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return quizSessionRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing((QuizSession quizSession) -> quizSession.getCreatedAt()).reversed())
                .map(QuizSessionMapper::toResponse)
                .toList();
    }
}
