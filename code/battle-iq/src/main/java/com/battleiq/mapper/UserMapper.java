package com.battleiq.mapper;

import com.battleiq.domain.entity.User;
import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.UserResponseDTO;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDTO toResponse(User user) {
        UserProfile profile = user.getProfile();
        UserResponseDTO.UserResponseDTOBuilder response = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail());
        if (profile != null) {
            response.fullName(profile.getFullName())
                    .avatarUrl(profile.getAvatarUrl())
                    .totalScore(profile.getTotalScore())
                    .level(profile.getLevel())
                    .currentStreak(profile.getCurrentStreak());
        }
        return response.build();
    }
}
