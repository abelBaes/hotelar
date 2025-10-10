package br.ifsp.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        String id,
        String roomId,
        String guestName,
        LocalDateTime checkin,
        LocalDateTime checkout,
        String status,
        List<String> extraServices
) {}
