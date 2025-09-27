package br.ifsp.demo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private final Room room;
    private final Guest guest;
    private final LocalDateTime checkIn;
    private final LocalDateTime checkOut;

    public Reservation(Room room, Guest guest, LocalDateTime checkIn, LocalDateTime checkOut) {
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Room getRoom() {
        return room;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }
}
