package br.ifsp.demo.dto.mapper;

import br.ifsp.demo.domain.model.Room;
import br.ifsp.demo.dto.request.CreateRoomRequest;
import br.ifsp.demo.dto.response.RoomResponse;
import org.springframework.stereotype.Component;

@Component
public class RoomDtoMapper {

    public Room toRoom(CreateRoomRequest request) {
        return new Room(request.id(), request.status(), request.price());
    }

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(room.getId(), room.getStatus(), room.getPrice());
    }
}
