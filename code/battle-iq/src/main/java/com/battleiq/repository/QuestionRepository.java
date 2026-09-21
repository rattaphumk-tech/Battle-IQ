package com.battleiq.repository;

import com.battleiq.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategoryId(Long categoryId);

    // Custom Query สำหรับสุ่มคำถามตามหมวดหมู่
    @Query(value = "SELECT * FROM questions WHERE category_id = :categoryId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") int limit);
}