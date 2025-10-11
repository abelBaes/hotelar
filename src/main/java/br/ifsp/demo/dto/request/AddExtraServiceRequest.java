package br.ifsp.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddExtraServiceRequest(
        @Schema(description = "Service name", example = "Spa")
        String name,
        @Schema(description = "Service value", example = "150.0")
        double value
) {}
