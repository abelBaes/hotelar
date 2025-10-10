package br.ifsp.demo.persistence.repository;

import br.ifsp.demo.domain.model.Reservation;
import br.ifsp.demo.domain.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    void save(Room room);

    void update(Room newRoom);

    Optional<Room> findById(String id);

    List<Room> findAll();
}
