package br.ifsp.demo.service;

import br.ifsp.demo.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();

    public Reservation createReservation(Room room, Guest guest, LocalDateTime checkIn, LocalDateTime checkOut) {
        validateGuest(guest);
        validateRoom(room);
        validateDates(checkIn, checkOut);
        validateAvailability(room, checkIn, checkOut);

        Reservation reservation = new Reservation(room, guest, checkIn, checkOut);
        reservations.add(reservation);
        room.setStatus(Status.RESERVED);

        return reservation;
    }

    private void validateGuest(Guest guest) {
        Objects.requireNonNull(guest, "Guest must not be null");

        if (guest.getCpf() == null || guest.getCpf().isBlank()
                || guest.getName() == null || guest.getName().isBlank()
                || guest.getAge() == null) {
            throw new IllegalArgumentException("Guest must provide valid CPF, name and age");
        }

        if (guest.getAge() < 18) {
            throw new IllegalArgumentException("Guest must be at least 18 years old to make a reservation");
        }
    }

    private void validateRoom(Room room) {
        Objects.requireNonNull(room, "Room does not exist");

        if (room.getStatus() == Status.UNDER_MAINTENANCE) {
            throw new IllegalStateException("Room is under maintenance");
        }
    }

    private void validateDates(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        if (checkIn.isBefore(LocalDateTime.now()) || checkOut.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation dates must be in the future");
        }
    }

    private void validateAvailability(Room room, LocalDateTime checkIn, LocalDateTime checkOut) {
        for (Reservation existing : reservations) {
            if (existing.getRoom().getId().equals(room.getId())) {
                boolean overlap = checkIn.isBefore(existing.getCheckOut())
                        && checkOut.isAfter(existing.getCheckIn());
                if (overlap) {
                    throw new IllegalStateException("Room " + room.getId() + " not available for the selected period");
                }
            }
        }
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }
}
