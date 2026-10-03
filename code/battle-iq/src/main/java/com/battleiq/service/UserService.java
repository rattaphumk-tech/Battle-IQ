package com.battleiq.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.User;
import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.UserRequestDTO;
import com.battleiq.dto.UserResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * สมัครสมาชิกใหม่ พร้อมสร้าง UserProfile เริ่มต้น (One-to-One)
     */
    @Transactional
    public UserResponseDTO register(UserRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .build();
        user.setProfile(profile);

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return toResponse(user);
    }

    private UserResponseDTO toResponse(User user) {
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
