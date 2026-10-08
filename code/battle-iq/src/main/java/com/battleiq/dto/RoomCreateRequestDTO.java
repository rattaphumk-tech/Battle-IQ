package com.battleiq.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCreateRequestDTO {

    @NotNull(message = "Host ID is required")
    private Long hostId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
