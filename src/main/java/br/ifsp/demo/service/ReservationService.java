package br.ifsp.demo.service;

import br.ifsp.demo.domain.Guest;
import br.ifsp.demo.domain.Reservation;
import br.ifsp.demo.domain.Room;
import br.ifsp.demo.domain.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();

    public Reservation createReservation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        Reservation reservation = new Reservation(room, guest, checkIn, checkOut);
        reservations.add(reservation);
        room.setStatus(Status.RESERVED);
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

}
