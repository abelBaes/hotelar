package br.ifsp.demo.persistence.repository.reservation;

import br.ifsp.demo.domain.model.Reservation;
import br.ifsp.demo.persistence.mapper.ReservationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReservationJpaAdapter implements ReservationRepository {

    private final SpringReservationRepository repository;

    public ReservationJpaAdapter(SpringReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Reservation reservation) {
        repository.save(ReservationMapper.toEntity(reservation));
    }

    @Override
    public void update(Reservation reservation) {
        repository.save(ReservationMapper.toEntity(reservation));
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return repository.findById(id)
                .map(ReservationMapper::toDomain);
    }

    @Override
    public List<Reservation> findAll() {
        return repository.findAll()
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }
}
