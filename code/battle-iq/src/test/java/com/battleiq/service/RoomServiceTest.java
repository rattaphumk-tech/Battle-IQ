package com.battleiq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.battleiq.service.impl.RoomServiceImpl;
import com.battleiq.domain.entity.Category;
import com.battleiq.domain.entity.Question;
import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.Room;
import com.battleiq.domain.entity.RoomPlayer;
import com.battleiq.domain.entity.User;
import com.battleiq.domain.enums.RoomStatus;
import com.battleiq.dto.request.RoomCreateRequestDTO;
import com.battleiq.dto.response.RoomResponseDTO;
import com.battleiq.exception.ConflictException;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.repository.RoomRepository;
import com.battleiq.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private QuestionFactory questionFactory;
    @Mock
    private QuizSessionService quizSessionService;

    @InjectMocks
    private RoomServiceImpl roomService;

    private final Category category = Category.builder().id(3L).name("Science").build();
    private final User host = User.builder().id(1L).username("host").build();
    private final User guest = User.builder().id(2L).username("guest").build();

    private Room roomWith(RoomStatus status, User... users) {
        Room room = Room.builder().id(10L).code("ABC123").host(host).category(category).status(status).build();
        for (User u : users) {
            room.getPlayers().add(RoomPlayer.builder().room(room).user(u).build());
        }
        return room;
    }

    // TC-R01 สร้างห้อง ได้รหัส 6 ตัว และเจ้าของห้องอยู่ในรายชื่อผู้เล่น
    @Test
    void createRoomAddsHostAsPlayerWithSixCharCode() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(host));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(roomRepository.existsByCode(anyString())).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomCreateRequestDTO request = new RoomCreateRequestDTO();
        request.setHostId(1L);
        request.setCategoryId(3L);

        RoomResponseDTO response = roomService.createRoom(request);

        assertEquals(6, response.getCode().length());
        assertTrue(response.getCode().matches("[A-Z2-9]+"));
        assertEquals("WAITING", response.getStatus());
        assertEquals(1, response.getPlayers().size());
        assertTrue(response.getPlayers().get(0).getHost());
    }

    // TC-R02 เข้าห้องซ้ำ ไม่เพิ่มคนซ้ำ
    @Test
    void joinRoomTwiceDoesNotDuplicatePlayer() {
        Room room = roomWith(RoomStatus.WAITING, host, guest);
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(guest));

        RoomResponseDTO response = roomService.joinRoom("abc123", 2L);

        assertEquals(2, response.getPlayers().size());
        verify(roomRepository, never()).save(any(Room.class));
    }

    // TC-R03 ห้องเริ่มแล้ว เข้าไม่ได้
    @Test
    void joinRoomRejectedWhenAlreadyStarted() {
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(roomWith(RoomStatus.IN_PROGRESS, host)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(guest));

        assertThrows(ConflictException.class, () -> roomService.joinRoom("ABC123", 2L));
    }

    // TC-R04 คนที่ไม่ใช่เจ้าของห้องกดเริ่มไม่ได้
    @Test
    void startRoomRejectedForNonHost() {
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(roomWith(RoomStatus.WAITING, host, guest)));

        assertThrows(ConflictException.class, () -> roomService.startRoom("ABC123", 2L));
        verify(quizSessionService, never()).createSession(any(), any(), any());
    }

    // TC-R05 มีคนเดียว เริ่มไม่ได้
    @Test
    void startRoomNeedsAtLeastTwoPlayers() {
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(roomWith(RoomStatus.WAITING, host)));

        assertThrows(ConflictException.class, () -> roomService.startRoom("ABC123", 1L));
    }

    // TC-R06 เริ่มแข่ง สร้าง session ให้ทุกคนด้วยคำถามชุดเดียวกัน
    @Test
    void startRoomCreatesSessionForEveryPlayerWithSameQuestions() {
        Room room = roomWith(RoomStatus.WAITING, host, guest);
        List<Question> questions = List.of(Question.builder().id(7L).build(), Question.builder().id(8L).build());
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(room));
        when(questionFactory.createQuizQuestions(eq(3L), anyInt())).thenReturn(questions);
        when(quizSessionService.createSession(any(User.class), eq(category), eq(questions)))
                .thenAnswer(inv -> QuizSession.builder().id(100L).status("IN_PROGRESS").build());
        when(roomRepository.save(room)).thenReturn(room);

        RoomResponseDTO response = roomService.startRoom("ABC123", 1L);

        assertEquals("IN_PROGRESS", response.getStatus());
        verify(questionFactory, times(1)).createQuizQuestions(anyLong(), anyInt());
        verify(quizSessionService, times(2)).createSession(any(User.class), eq(category), eq(questions));
        assertTrue(response.getPlayers().stream().allMatch(p -> p.getSessionId() == 100L));
    }

    // TC-R07 ทุกคนเล่นจบ ห้องเปลี่ยนเป็น FINISHED และเรียงคะแนนมากไปน้อย
    @Test
    void getRoomMarksFinishedAndRanksPlayers() {
        Room room = roomWith(RoomStatus.IN_PROGRESS, host, guest);
        room.getPlayers().get(0).setQuizSession(QuizSession.builder().id(1L).status("COMPLETED").totalScore(40).build());
        room.getPlayers().get(1).setQuizSession(QuizSession.builder().id(2L).status("COMPLETED").totalScore(90).build());
        when(roomRepository.findByCode("ABC123")).thenReturn(Optional.of(room));

        RoomResponseDTO response = roomService.getRoom("ABC123");

        assertEquals("FINISHED", response.getStatus());
        assertEquals(2L, response.getPlayers().get(0).getUserId());
        assertEquals(90, response.getPlayers().get(0).getScore());
    }
}
