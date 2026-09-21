package com.battleiq.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    private Integer totalQuestions = 0;

    @Builder.Default
    private Integer totalScore = 0;

    @Column(nullable = false)
    private String status; // "IN_PROGRESS", "COMPLETED", "ABANDONED"

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "quizSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "IN_PROGRESS";
        }
    }
}