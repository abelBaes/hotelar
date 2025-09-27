package br.ifsp.demo.service;
import br.ifsp.demo.domain.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();

    public Reservation createReservation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation existing : reservations) {
            if (existing.getRoom().getId().equals(room.getId())) {
                boolean overlap = checkIn.isBefore(existing.getCheckOut())
                        && checkOut.isAfter(existing.getCheckIn());
                if (overlap) {
                    throw new IllegalStateException("Room " + room.getId() + " not available for the selected period");
                }
            }
        }

        Reservation reservation = new Reservation(room, guest, checkIn, checkOut);
        reservations.add(reservation);
        room.setStatus(Status.RESERVED);
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }
}
