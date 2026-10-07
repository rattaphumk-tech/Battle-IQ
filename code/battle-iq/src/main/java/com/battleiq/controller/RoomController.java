package com.battleiq.controller;

import com.battleiq.dto.RoomCreateRequestDTO;
import com.battleiq.dto.RoomJoinRequestDTO;
import com.battleiq.dto.RoomResponseDTO;
import com.battleiq.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "ห้องแข่งขันหลายคน")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "สร้างห้อง เจ้าของห้องเข้าร่วมอัตโนมัติ")
    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(@Valid @RequestBody RoomCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @Operation(summary = "ดูสถานะห้องและผู้เล่น ใช้ polling ระหว่างรอและระหว่างแข่ง")
    @GetMapping("/{code}")
    public ResponseEntity<RoomResponseDTO> getRoom(@PathVariable("code") String code) {
        return ResponseEntity.ok(roomService.getRoom(code));
    }

    @Operation(summary = "เข้าร่วมห้องด้วยรหัส")
    @PostMapping("/{code}/join")
    public ResponseEntity<RoomResponseDTO> joinRoom(@PathVariable("code") String code,
                                                    @Valid @RequestBody RoomJoinRequestDTO request) {
        return ResponseEntity.ok(roomService.joinRoom(code, request.getUserId()));
    }

    @Operation(summary = "เจ้าของห้องกดเริ่มแข่ง")
    @PostMapping("/{code}/start")
    public ResponseEntity<RoomResponseDTO> startRoom(@PathVariable("code") String code,
                                                     @Valid @RequestBody RoomJoinRequestDTO request) {
        return ResponseEntity.ok(roomService.startRoom(code, request.getUserId()));
    }
}
