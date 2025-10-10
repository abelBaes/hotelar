package br.ifsp.demo.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record CreateReservationRequest(
        @NotBlank String roomId,
        @NotNull GuestDTO guest,
        @NotNull LocalDateTime checkin,
        @NotNull LocalDateTime checkout
) {}