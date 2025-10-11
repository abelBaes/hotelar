package br.ifsp.demo.dto.request;

import br.ifsp.demo.domain.model.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRoomRequest(
        @Schema(description = "Room ID (3 digits)", example = "101")
        String id,
        @Schema(description = "Room status", example = "AVAILABLE")
        RoomStatus status,
        @Schema(description = "Room price", example = "250.0")
        double price
) {}