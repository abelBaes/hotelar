package br.ifsp.demo.persistence.repository.room;

import br.ifsp.demo.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringRoomRepository extends JpaRepository<RoomEntity, String> {
}
