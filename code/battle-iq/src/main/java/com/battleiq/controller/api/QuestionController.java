package com.battleiq.controller.api;

import com.battleiq.dto.response.QuestionDTO;
import com.battleiq.dto.response.QuestionDetailDTO;
import com.battleiq.dto.request.QuestionRequestDTO;
import com.battleiq.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "จัดการคลังคำถาม")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "ดึงคำถามแบบแบ่งหน้า เช่น ?page=0&size=10&sort=id,desc")
    @GetMapping
    public ResponseEntity<Page<QuestionDetailDTO>> getQuestions(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(questionService.getQuestions(pageable));
    }

    @Operation(summary = "ดึงคำถามตามหมวดหมู่ (ไม่ส่งเฉลย)")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCategory(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(categoryId));
    }

    @Operation(summary = "เพิ่มคำถาม")
    @PostMapping
    public ResponseEntity<QuestionDetailDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @Operation(summary = "แก้ไขคำถาม")
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDetailDTO> updateQuestion(@PathVariable("id") Long id,
                                                            @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request));
    }

    @Operation(summary = "ลบคำถาม")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}