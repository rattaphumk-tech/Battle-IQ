package com.battleiq.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.Room;
import com.battleiq.domain.entity.RoomPlayer;
import com.battleiq.domain.entity.User;
import com.battleiq.domain.enums.RoomStatus;
import com.battleiq.dto.RoomCreateRequestDTO;
import com.battleiq.dto.RoomResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.exception.ResourceNotFoundException;
import com.battleiq.mapper.RoomMapper;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.RoomRepository;
import com.battleiq.repository.UserRepository;
import com.battleiq.service.QuestionFactory;
import com.battleiq.service.QuizSessionService;
import com.battleiq.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final int MAX_PLAYERS = 8;
    private static final int CODE_LENGTH = 6;
    // ตัดตัวอักษรที่สับสนง่าย (0/O, 1/I) ออก
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionFactory questionFactory;
    private final QuizSessionService quizSessionService;

    @Override
    @Transactional
    public RoomResponseDTO createRoom(RoomCreateRequestDTO request) {
        User host = findUser(request.getHostId());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Room room = Room.builder()
                .code(generateCode())
                .host(host)
                .category(category)
                .build();
        room.getPlayers().add(RoomPlayer.builder().room(room).user(host).build());

        return RoomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponseDTO joinRoom(String code, Long userId) {
        Room room = findRoom(code);
        User user = findUser(userId);

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new ConflictException("Room " + code + " has already started");
        }
        boolean alreadyIn = room.getPlayers().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
        if (alreadyIn) {
            return RoomMapper.toResponse(room);
        }
        if (room.getPlayers().size() >= MAX_PLAYERS) {
            throw new ConflictException("Room " + code + " is full");
        }

        room.getPlayers().add(RoomPlayer.builder().room(room).user(user).build());
        return RoomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponseDTO getRoom(String code) {
        Room room = findRoom(code);
        // ทุกคนเล่นจบแล้วถือว่าห้องจบ
        if (room.getStatus() == RoomStatus.IN_PROGRESS && allFinished(room)) {
            room.setStatus(RoomStatus.FINISHED);
        }
        return RoomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponseDTO startRoom(String code, Long userId) {
        Room room = findRoom(code);
        if (!room.getHost().getId().equals(userId)) {
            throw new ConflictException("Only the host can start the room");
        }
        if (room.getStatus() != RoomStatus.WAITING) {
            throw new ConflictException("Room " + code + " has already started");
        }
        if (room.getPlayers().size() < 2) {
            throw new ConflictException("Need at least 2 players to start");
        }

        List<Question> questions = questionFactory.createQuizQuestions(
                room.getCategory().getId(), QuizSessionService.QUESTIONS_PER_SESSION);

        for (RoomPlayer player : room.getPlayers()) {
            QuizSession session = quizSessionService.createSession(player.getUser(), room.getCategory(), questions);
            player.setQuizSession(session);
        }

        room.setStatus(RoomStatus.IN_PROGRESS);
        room.setStartedAt(LocalDateTime.now());
        return RoomMapper.toResponse(roomRepository.save(room));
    }

    private boolean allFinished(Room room) {
        return room.getPlayers().stream()
                .allMatch(p -> p.getQuizSession() != null && "COMPLETED".equals(p.getQuizSession().getStatus()));
    }

    private Room findRoom(String code) {
        return roomRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with code: " + code));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private String generateCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (roomRepository.existsByCode(code));
        return code;
    }
}
