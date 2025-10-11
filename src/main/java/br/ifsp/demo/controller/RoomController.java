package br.ifsp.demo.controller;

import br.ifsp.demo.domain.model.RoomStatus;
import br.ifsp.demo.domain.service.RoomService;
import br.ifsp.demo.dto.mapper.RoomDtoMapper;
import br.ifsp.demo.dto.request.CreateRoomRequest;
import br.ifsp.demo.dto.response.RoomResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;
    private final RoomDtoMapper roomDtoMapper;

    public RoomController(RoomService roomService, RoomDtoMapper roomDtoMapper) {
        this.roomService = roomService;
        this.roomDtoMapper = roomDtoMapper;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        var room = roomService.createRoom(request.id(), request.status(), request.price());
        var response = roomDtoMapper.toResponse(room);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable String id) {
        var room = roomService.findRoomById(id);
        var response = roomDtoMapper.toResponse(room);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateRoomStatus(@PathVariable String id, @RequestParam RoomStatus status) {
        roomService.updateRoomStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Void> updateRoomPrice(@PathVariable String id, @RequestParam double price) {
        roomService.updatePrice(id, price);
        return ResponseEntity.noContent().build();
    }
}
