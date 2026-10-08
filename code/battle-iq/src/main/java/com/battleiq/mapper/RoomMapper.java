package com.battleiq.mapper;

import java.util.Comparator;
import java.util.List;

import com.battleiq.domain.entity.QuizSession;
import com.battleiq.domain.entity.Room;
import com.battleiq.domain.entity.RoomPlayer;
import com.battleiq.dto.response.RoomPlayerDTO;
import com.battleiq.dto.response.RoomResponseDTO;

public final class RoomMapper {

    private RoomMapper() {
    }

    public static RoomResponseDTO toResponse(Room room) {
        List<RoomPlayerDTO> players = room.getPlayers().stream()
                .map(p -> toPlayerDTO(p, room))
                .sorted(Comparator.comparing((RoomPlayerDTO p) -> p.getScore() == null ? -1 : p.getScore()).reversed())
                .toList();

        return RoomResponseDTO.builder()
                .code(room.getCode())
                .status(room.getStatus().name())
                .hostId(room.getHost().getId())
                .categoryId(room.getCategory().getId())
                .categoryName(room.getCategory().getName())
                .createdAt(room.getCreatedAt())
                .players(players)
                .build();
    }

    public static RoomPlayerDTO toPlayerDTO(RoomPlayer player, Room room) {
        QuizSession session = player.getQuizSession();
        boolean finished = session != null && "COMPLETED".equals(session.getStatus());
        return RoomPlayerDTO.builder()
                .userId(player.getUser().getId())
                .username(player.getUser().getUsername())
                .fullName(player.getUser().getProfile() != null ? player.getUser().getProfile().getFullName() : null)
                .host(player.getUser().getId().equals(room.getHost().getId()))
                .sessionId(session != null ? session.getId() : null)
                .score(finished ? session.getTotalScore() : null)
                .finished(finished)
                .build();
    }
}
