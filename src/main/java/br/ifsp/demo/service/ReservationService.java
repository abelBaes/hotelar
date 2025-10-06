package br.ifsp.demo.service;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.*;

public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Room room, Guest guest, StayPeriod stayPeriod) {
        validateGuest(guest);
        validateRoom(room);
        validateStayPeriod(stayPeriod);
        validateAvailability(room, stayPeriod);

        Reservation reservation = new Reservation(ReservationIDService.generate(LocalDateTime.now()),
                room,
                guest,
                stayPeriod,
                ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);

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

        if (room.getStatus() == RoomStatus.UNDER_MAINTENANCE) {
            throw new IllegalStateException("Room is under maintenance");
        }
    }

    private void validateStayPeriod(StayPeriod stayPeriod) {
        LocalDateTime checkIn = stayPeriod.getCheckin();
        LocalDateTime checkOut = stayPeriod.getCheckout();

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

    }

    private void validateAvailability(Room room, StayPeriod stayPeriod) {

        for (Reservation savedReservation : reservationRepository.findAll()) {
            if (savedReservation.getRoom().getId().equals(room.getId())) {
                boolean overlap = stayPeriod.getCheckin().isBefore(savedReservation.getStayPeriod().getCheckout())
                        && stayPeriod.getCheckout().isAfter(savedReservation.getStayPeriod().getCheckin());
                if (overlap) {
                    throw new IllegalStateException("Room " + room.getId() + " not available for the selected period");
                }
            }
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation addExtraService(String reservationId, ExtraService service){
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (reservation.isEmpty()) throw new NoSuchElementException("Reservation not found for id: " + reservationId);

        reservation.get().appendExtraService(service);
        reservationRepository.update(reservation.get());
        return reservation.get();
    }
}
