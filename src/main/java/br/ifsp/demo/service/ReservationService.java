package br.ifsp.demo.service;

import br.ifsp.demo.domain.Guest;
import br.ifsp.demo.domain.Reservation;
import br.ifsp.demo.domain.Room;

import java.time.LocalDate;

public class ReservationService {


    public Reservation createReservation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        return new Reservation(room, guest, checkIn, checkOut);
    }


}
