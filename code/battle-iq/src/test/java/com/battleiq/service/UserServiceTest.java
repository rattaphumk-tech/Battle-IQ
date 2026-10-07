package com.battleiq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.battleiq.service.impl.UserServiceImpl;
import com.battleiq.domain.entity.User;
import com.battleiq.domain.entity.UserProfile;
import com.battleiq.dto.LoginRequestDTO;
import com.battleiq.dto.UserRequestDTO;
import com.battleiq.dto.UserResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.InvalidCredentialsException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new UserRequestDTO();
        registerRequest.setUsername("somchai");
        registerRequest.setEmail("somchai@example.com");
        registerRequest.setPassword("secret123");
        registerRequest.setFullName("Somchai Jaidee");
    }

    private User savedUser(String rawPassword) {
        User user = User.builder().id(1L).username("somchai").email("somchai@example.com")
                .password("hashed-" + rawPassword).build();
        user.setProfile(UserProfile.builder().user(user).fullName("Somchai Jaidee").build());
        return user;
    }

    // TC-U01 สมัครสำเร็จ ต้องสร้างโปรไฟล์และเข้ารหัสรหัสผ่าน
    @Test
    void registerCreatesUserWithProfileAndHashedPassword() {
        when(userRepository.existsByUsername("somchai")).thenReturn(false);
        when(userRepository.existsByEmail("somchai@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponseDTO response = userService.register(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("hashed-secret123", saved.getPassword());
        assertNotEquals("secret123", saved.getPassword());
        assertNotNull(saved.getProfile());
        assertEquals("Somchai Jaidee", saved.getProfile().getFullName());

        assertEquals(1L, response.getId());
        assertEquals(1, response.getLevel());
        assertEquals(0, response.getTotalScore());
    }

    // TC-U02 ชื่อผู้ใช้ซ้ำ ต้องปฏิเสธและไม่บันทึก
    @Test
    void registerRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("somchai")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    // TC-U03 อีเมลซ้ำ ต้องปฏิเสธและไม่บันทึก
    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByUsername("somchai")).thenReturn(false);
        when(userRepository.existsByEmail("somchai@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    // TC-U04 login ถูกต้อง
    @Test
    void loginReturnsUserWhenPasswordMatches() {
        when(userRepository.findByUsername("somchai")).thenReturn(Optional.of(savedUser("secret123")));
        when(passwordEncoder.matches("secret123", "hashed-secret123")).thenReturn(true);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("somchai");
        request.setPassword("secret123");

        UserResponseDTO response = userService.login(request);

        assertEquals(1L, response.getId());
        assertEquals("somchai", response.getUsername());
    }

    // TC-U05 รหัสผ่านผิด
    @Test
    void loginRejectsWrongPassword() {
        when(userRepository.findByUsername("somchai")).thenReturn(Optional.of(savedUser("secret123")));
        when(passwordEncoder.matches("wrong", "hashed-secret123")).thenReturn(false);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("somchai");
        request.setPassword("wrong");

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    // TC-U06 ไม่มีผู้ใช้นี้ ต้องตอบแบบเดียวกับรหัสผิด ไม่เปิดเผยว่ามีบัญชีหรือไม่
    @Test
    void loginRejectsUnknownUserWithSameError() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("nobody");
        request.setPassword("secret123");

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> userService.login(request));
        assertEquals("Invalid username or password", ex.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    // TC-U07 หา user ไม่เจอ
    @Test
    void getUserByIdThrowsWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    // TC-U08 user ที่ไม่มีโปรไฟล์ ต้องไม่พัง และส่งค่าโปรไฟล์เป็น null
    @Test
    void getUserByIdWorksWithoutProfile() {
        User user = User.builder().id(2L).username("noprofile").email("n@example.com").password("x").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(2L);

        assertEquals("noprofile", response.getUsername());
        assertEquals(null, response.getLevel());
    }
}
