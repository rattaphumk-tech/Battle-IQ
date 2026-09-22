package com.battleiq.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.battleiq.domain.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategoryId(Long categoryId);

    // Custom Query สำหรับสุ่มคำถามตามหมวดหมู่
    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}