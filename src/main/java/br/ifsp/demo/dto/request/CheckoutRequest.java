package br.ifsp.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CheckoutRequest(
        @Schema(description = "Checkout date", example = "2025-10-18T12:00:00")
        String checkoutDate
) {}
