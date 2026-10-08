package com.battleiq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.battleiq.service.impl.UserProfileServiceImpl;
import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.request.ProfileUpdateDTO;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfile profile(int totalScore, int streak) {
        return UserProfile.builder().id(1L).totalScore(totalScore).level(1).currentStreak(streak).build();
    }

    @Test
    void applyGameResultAddsScoreAndRaisesLevel() {
        UserProfile profile = profile(80, 2);
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        userProfileService.applyGameResult(1L, 45);

        assertEquals(125, profile.getTotalScore());
        assertEquals(2, profile.getLevel());
        assertEquals(3, profile.getCurrentStreak());
        verify(userProfileRepository).save(profile);
    }

    @Test
    void applyGameResultResetsStreakWhenScoreIsZero() {
        UserProfile profile = profile(80, 4);
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        userProfileService.applyGameResult(1L, 0);

        assertEquals(80, profile.getTotalScore());
        assertEquals(0, profile.getCurrentStreak());
    }

    @Test
    void updateProfileChangesNameAndAvatar() {
        UserProfile profile = profile(0, 0);
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setFullName("Somchai");
        request.setAvatarUrl("https://example.com/a.png");

        userProfileService.updateProfile(1L, request);

        assertEquals("Somchai", profile.getFullName());
        assertEquals("https://example.com/a.png", profile.getAvatarUrl());
    }

    @Test
    void applyGameResultThrowsWhenProfileMissing() {
        when(userProfileRepository.findByUserId(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.applyGameResult(9L, 10));
    }
}
