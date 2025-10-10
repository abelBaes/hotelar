package br.ifsp.demo.persistence.repository.room;

import br.ifsp.demo.domain.model.Room;
import br.ifsp.demo.persistence.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RoomJpaAdapter implements RoomRepository {

    private final SpringRoomRepository repository;

    public RoomJpaAdapter(SpringRoomRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Room room) {
        repository.save(RoomMapper.toEntity(room));
    }

    @Override
    public void update(Room newRoom) {
        repository.save(RoomMapper.toEntity(newRoom));
    }

    @Override
    public Optional<Room> findById(String id) {
        return repository.findById(id)
                .map(RoomMapper::toDomain);
    }

    @Override
    public List<Room> findAll() {
        return repository.findAll()
                .stream()
                .map(RoomMapper::toDomain)
                .collect(Collectors.toList());
    }
}