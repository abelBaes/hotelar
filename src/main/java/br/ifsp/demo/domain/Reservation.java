package br.ifsp.demo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reservation {

    private final String id;
    private final Room room;
    private final Guest guest;
    private final StayPeriod stayPeriod;
    private final ReservationStatus reservationStatus;
    private final List<ExtraService> extraServices = new ArrayList<>();

    public Reservation(String id, Room room, Guest guest, StayPeriod stayPeriod, ReservationStatus reservationStatus) {
        this.id = id;
        this.room = room;
        this.guest = guest;
        this.stayPeriod = stayPeriod;
        this.reservationStatus = reservationStatus;
    }

    public String getId() { return id; }

    public Room getRoom() {
        return room;
    }

    public Guest getGuest() {
        return guest;
    }

    public StayPeriod getStayPeriod() { return stayPeriod; }

    public ReservationStatus getReservationStatus() { return reservationStatus; }

    public List<ExtraService> getExtraServices() {
        return extraServices;
    }

    public void appendExtraService(ExtraService extraService){
        this.extraServices.add(extraService);
    }
}
