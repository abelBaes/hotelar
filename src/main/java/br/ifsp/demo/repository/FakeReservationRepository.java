package br.ifsp.demo.repository;

import br.ifsp.demo.domain.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeReservationRepository implements ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public void update(Reservation newReservation) {
        reservations.removeIf(reservation -> reservation.getId().equals(newReservation.getId()));
        reservations.add(newReservation);
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return reservations.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Reservation> findAll() {
        return reservations;
    }
}
