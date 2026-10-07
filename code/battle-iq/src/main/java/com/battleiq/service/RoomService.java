package com.battleiq.service;

import com.battleiq.dto.RoomCreateRequestDTO;
import com.battleiq.dto.RoomResponseDTO;

public interface RoomService {

    RoomResponseDTO createRoom(RoomCreateRequestDTO request);

    RoomResponseDTO joinRoom(String code, Long userId);

    RoomResponseDTO getRoom(String code);

    /**
     * เจ้าของห้องกดเริ่ม สุ่มคำถามชุดเดียวแล้วสร้าง session ให้ผู้เล่นทุกคนด้วยคำถามชุดเดียวกัน
     */
    RoomResponseDTO startRoom(String code, Long userId);
}
