package com.battleiq.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.request.ProfileUpdateDTO;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.UserProfileRepository;
import com.battleiq.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private static final int SCORE_PER_LEVEL = 100;

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public void applyGameResult(Long userId, Integer score) {
        UserProfile profile = findProfile(userId);

        int totalScore = profile.getTotalScore() + score;
        profile.setTotalScore(totalScore);
        profile.setLevel(totalScore / SCORE_PER_LEVEL + 1);
        // ได้คะแนน = นับ streak ต่อ, ได้ 0 = เริ่มนับใหม่
        profile.setCurrentStreak(score > 0 ? profile.getCurrentStreak() + 1 : 0);

        userProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDTO request) {
        UserProfile profile = findProfile(userId);
        profile.setFullName(request.getFullName());
        profile.setAvatarUrl(request.getAvatarUrl());
        userProfileRepository.save(profile);
    }

    private UserProfile findProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));
    }
}
