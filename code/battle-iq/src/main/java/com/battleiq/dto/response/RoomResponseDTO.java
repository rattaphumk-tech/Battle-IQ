package com.battleiq.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponseDTO {
    private String code;
    private String status;
    private Long hostId;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private List<RoomPlayerDTO> players;
}
