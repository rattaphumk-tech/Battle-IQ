package com.battleiq.repository;

import com.battleiq.domain.entity.QuizDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizDetailRepository extends JpaRepository<QuizDetail, Long> {
    List<QuizDetail> findByQuizSessionId(Long sessionId);
}