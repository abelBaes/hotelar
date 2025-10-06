package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Reservation;
import br.ifsp.demo.domain.ReservationStatus;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    void save(Reservation reservation);

    Optional<Reservation> findById(String id);

    List<Reservation> findAll();
}
