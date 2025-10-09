package br.ifsp.demo.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reservation {

    private final String id;
    private final Room room;
    private final Guest guest;
    private StayPeriod stayPeriod;
    private ReservationStatus reservationStatus;
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

    public void setStayPeriod(StayPeriod stayPeriod) {
        this.stayPeriod = stayPeriod;
    }

    public void setReservationStatus(ReservationStatus status){
        this.reservationStatus = status;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Reservation that = (Reservation) object;
        return Objects.equals(id, that.id);
    }
    
}
