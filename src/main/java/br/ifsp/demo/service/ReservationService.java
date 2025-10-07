package br.ifsp.demo.service;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReservationService {
    
    private static final double VIP_DISCOUNT_RATE = 0.85; // 15% discount for VIP guests
    
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

    private void validateReservationActiveState(Reservation reservation){
        if(reservation.getReservationStatus() != ReservationStatus.ACTIVE)
            throw new IllegalStateException("Reservation must be Active");
    }

    private void validateUpdateAvailability(Room room, StayPeriod stayPeriod, Reservation reservation) {

        for (Reservation savedReservation : reservationRepository.findAll()) {
            if (savedReservation.getRoom().getId().equals(room.getId()) && !reservation.equals(savedReservation)) {
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
        ReservationIDService.validate(reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found for id: " + reservationId));

        validateReservationStatusForExtraService(reservation);
        
        reservation.appendExtraService(service);
        reservationRepository.update(reservation);
        return reservation;
    }

    public double checkout(String reservationId, LocalDateTime checkoutDate) {
        validateCheckoutDate(checkoutDate);
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        validateReservationStatusForCheckout(reservation);
        
        long nights = calculateNights(reservation.getStayPeriod(), checkoutDate);
        double baseAmount = calculateBaseAmount(reservation.getRoom(), nights);
        double extraServicesAmount = calculateExtraServicesAmount(reservation.getExtraServices());
        double totalAmount = baseAmount + extraServicesAmount;
        
        double finalAmount = applyVipDiscount(totalAmount, reservation.getGuest().isVip());
        
        finalizeReservation(reservation);
        
        return finalAmount;
    }
    
    private long calculateNights(StayPeriod stayPeriod, LocalDateTime checkoutDate) {
        return ChronoUnit.DAYS.between(
            stayPeriod.getCheckin().toLocalDate(),
            checkoutDate.toLocalDate()
        );
    }
    
    private double calculateBaseAmount(Room room, long nights) {
        return room.getPrice() * nights;
    }
    
    private double calculateExtraServicesAmount(List<ExtraService> extraServices) {
        return extraServices.stream()
            .mapToDouble(ExtraService::getValue)
            .sum();
    }
    
    private double applyVipDiscount(double totalAmount, boolean isVip) {
        return isVip ? totalAmount * VIP_DISCOUNT_RATE : totalAmount;
    }
    
    private void validateCheckoutDate(LocalDateTime checkoutDate) {
        if (checkoutDate == null) {
            throw new IllegalArgumentException("Checkout date cannot be null");
        }
        
        if (checkoutDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Checkout date cannot be in the past");
        }
    }
    
    private void validateReservationStatusForCheckout(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only active reservations can be checked out");
        }
    }
    
    private void validateReservationStatusForExtraService(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add services to finalized or canceled reservations");
        }
    }
    
    private void finalizeReservation(Reservation reservation) {
        Reservation finalizedReservation = new Reservation(
            reservation.getId(),
            reservation.getRoom(),
            reservation.getGuest(),
            reservation.getStayPeriod(),
            ReservationStatus.FINALIZED
        );
        
        for (ExtraService service : reservation.getExtraServices()) {
            finalizedReservation.appendExtraService(service);
        }
        
        reservationRepository.update(finalizedReservation);
    }

    public Reservation updateStayPeriod(String reservationId, StayPeriod newStayPeriod){
        validateStayPeriod(newStayPeriod);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found for id: " + reservationId));

        validateReservationActiveState(reservation);
        validateUpdateAvailability(reservation.getRoom(), newStayPeriod, reservation);

        reservation.setStayPeriod(newStayPeriod);
        reservationRepository.update(reservation);

        return reservation;
    }
}
