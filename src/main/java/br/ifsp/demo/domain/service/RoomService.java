package br.ifsp.demo.domain.service;

import br.ifsp.demo.domain.model.Room;
import br.ifsp.demo.domain.model.RoomStatus;
import br.ifsp.demo.persistence.repository.room.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(String id, RoomStatus status, double price) {
        validateRoomId(id);
        validatePrice(price);

        Room room = new Room(id, status, price);
        roomRepository.save(room);

        return room;
    }

    public Room findRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + id));
    }

    public void updateRoomStatus(String id, RoomStatus newStatus) {
        Room room = findRoomById(id);
        room.setStatus(newStatus);
        roomRepository.update(room);
    }

    public void updatePrice(String id, double newPrice) {
        validatePrice(newPrice);

        Room room = findRoomById(id);
        room.setPrice(newPrice);

        roomRepository.update(room);
    }

    private void validateRoomId(String roomId) {
        if (roomId == null || !roomId.matches("\\d{3}")) {
            throw new IllegalArgumentException("Invalid Room Id");
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Room price must be greater than zero");
        }
    }
}
