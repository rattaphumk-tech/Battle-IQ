package com.battleiq.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    
    private String avatarUrl;

    @Builder.Default
    private Integer totalScore = 0;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private Integer currentStreak = 0;
}