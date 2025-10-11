package br.ifsp.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateStayPeriodRequest(
        @Schema(description = "New check-in date", example = "2025-10-20T14:00:00")
        String checkin,
        @Schema(description = "New check-out date", example = "2025-10-23T12:00:00")
        String checkout
) {}
