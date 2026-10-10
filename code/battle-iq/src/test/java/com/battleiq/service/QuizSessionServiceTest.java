package com.battleiq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.battleiq.service.impl.QuizSessionServiceImpl;
import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.QuizDetail;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.User;
import com.battleiq.dto.response.QuizSessionResponseDTO;
import com.battleiq.event.GameCompletedEvent;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.QuizSessionRepository;
import com.battleiq.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class QuizSessionServiceTest {

    @Mock
    private QuizSessionRepository quizSessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private QuestionFactory questionFactory;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private QuizSessionServiceImpl quizSessionService;

    private QuizSession sessionWithScores(String status, int... scores) {
        QuizSession session = QuizSession.builder()
                .id(1L)
                .user(User.builder().id(7L).build())
                .category(Category.builder().id(2L).name("Science").build())
                .totalQuestions(scores.length)
                .status(status)
                .build();
        for (int score : scores) {
            session.getDetails().add(QuizDetail.builder().quizSession(session).scoreEarned(score).build());
        }
        return session;
    }

    @Test
    void completeSessionSumsScoreAndPublishesEvent() {
        QuizSession session = sessionWithScores("IN_PROGRESS", 20, 0, 15);
        when(quizSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(quizSessionRepository.save(session)).thenReturn(session);

        QuizSessionResponseDTO response = quizSessionService.completeSession(1L);

        assertEquals(35, response.getTotalScore());
        assertEquals("COMPLETED", response.getStatus());

        ArgumentCaptor<GameCompletedEvent> captor = ArgumentCaptor.forClass(GameCompletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals(7L, captor.getValue().getUserId());
        assertEquals(35, captor.getValue().getTotalScore());
    }

    @Test
    void completeSessionRejectsSessionThatAlreadyCompleted() {
        when(quizSessionRepository.findById(1L)).thenReturn(Optional.of(sessionWithScores("COMPLETED", 10)));

        assertThrows(ConflictException.class, () -> quizSessionService.completeSession(1L));
        verify(eventPublisher, never()).publishEvent(any(GameCompletedEvent.class));
    }

    @Test
    void completeSessionThrowsWhenSessionMissing() {
        when(quizSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> quizSessionService.completeSession(99L));
    }
}