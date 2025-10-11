package br.ifsp.demo.controller;

import br.ifsp.demo.domain.model.ExtraService;
import br.ifsp.demo.domain.model.StayPeriod;
import br.ifsp.demo.domain.service.ReservationService;
import br.ifsp.demo.domain.service.RoomService;
import br.ifsp.demo.dto.mapper.ReservationDtoMapper;
import br.ifsp.demo.dto.request.AddExtraServiceRequest;
import br.ifsp.demo.dto.request.CheckoutRequest;
import br.ifsp.demo.dto.request.CreateReservationRequest;
import br.ifsp.demo.dto.request.UpdateStayPeriodRequest;
import br.ifsp.demo.dto.response.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;
    private final ReservationDtoMapper reservationDtoMapper;

    public ReservationController(
            ReservationService reservationService,
            RoomService roomService,
            ReservationDtoMapper reservationDtoMapper
    ) {
        this.reservationService = reservationService;
        this.roomService = roomService;
        this.reservationDtoMapper = reservationDtoMapper;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        try {
            var room = roomService.findRoomById(request.roomId());
            var guest = reservationDtoMapper.toGuest(request);
            var stayPeriod = reservationDtoMapper.toStayPeriod(request);
            var reservation = reservationService.createReservation(room, guest, stayPeriod);
            var response = reservationDtoMapper.toResponse(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }

    @PutMapping("/{id}/extra-service")
    public ResponseEntity<?> addExtraService(
            @PathVariable String id,
            @Valid @RequestBody AddExtraServiceRequest request) {
        try {
            var service = new ExtraService(request.name(), request.value());
            var reservation = reservationService.addExtraService(id, service);
            var response = reservationDtoMapper.toResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(
            @PathVariable String id,
            @Valid @RequestBody CheckoutRequest request) {
        try {
            var checkoutDate = LocalDateTime.parse(request.checkoutDate());
            var amount = reservationService.checkout(id, checkoutDate);
            return ResponseEntity.ok(Map.of("totalAmount", amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/stay-period")
    public ResponseEntity<?> updateStayPeriod(
            @PathVariable String id,
            @Valid @RequestBody UpdateStayPeriodRequest request) {
        try {
            var checkin = LocalDateTime.parse(request.checkin());
            var checkout = LocalDateTime.parse(request.checkout());
            var stayPeriod = new StayPeriod(checkin, checkout);
            var reservation = reservationService.updateStayPeriod(id, stayPeriod);
            var response = reservationDtoMapper.toResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable String id) {
        try {
            var reservation = reservationService.cancelReservation(id);
            var response = reservationDtoMapper.toResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
