package br.ifsp.demo.controller;

import br.ifsp.demo.domain.service.ReservationService;
import br.ifsp.demo.domain.service.RoomService;
import br.ifsp.demo.dto.mapper.ReservationDtoMapper;
import br.ifsp.demo.dto.request.CreateReservationRequest;
import br.ifsp.demo.dto.response.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        try {
            var room = roomService.findRoomById(request.roomId());
            var guest = reservationDtoMapper.toGuest(request);
            var stayPeriod = reservationDtoMapper.toStayPeriod(request);
            var reservation = reservationService.createReservation(room, guest, stayPeriod);
            var response = reservationDtoMapper.toResponse(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
