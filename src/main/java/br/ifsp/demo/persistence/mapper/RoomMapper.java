package br.ifsp.demo.persistence.mapper;

import br.ifsp.demo.domain.model.Room;
import br.ifsp.demo.persistence.entity.RoomEntity;

public class RoomMapper {

    public static RoomEntity toEntity(Room domain) {
        RoomEntity entity = new RoomEntity();
        entity.setId(domain.getId());
        entity.setStatus(domain.getStatus());
        entity.setPrice(domain.getPrice());
        return entity;
    }

    public static Room toDomain(RoomEntity entity) {
        return new Room(
                entity.getId(),
                entity.getStatus(),
                entity.getPrice()
        );
    }
}
