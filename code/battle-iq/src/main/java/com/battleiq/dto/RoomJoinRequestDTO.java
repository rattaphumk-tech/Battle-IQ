package com.battleiq.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomJoinRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;
}
