package br.ifsp.demo.persistence.repository;

import br.ifsp.demo.domain.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository  {
    void save(Reservation reservation);

    void update(Reservation newReservation);

    Optional<Reservation> findById(String id);

    List<Reservation> findAll();
}
