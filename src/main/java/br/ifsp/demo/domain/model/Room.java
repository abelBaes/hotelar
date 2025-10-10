package br.ifsp.demo.domain.model;

public class Room {

    private final String id;
    private RoomStatus status;
    private double price;

    public Room(String id, RoomStatus status, double price) {
        this.id = id;
        this.status = status;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setPrice(double price) { this.price = price; }

    public double getPrice() {
        return price;
    }
}
