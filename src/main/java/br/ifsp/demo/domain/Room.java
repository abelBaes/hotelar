package br.ifsp.demo.domain;

public class Room {

    private final String id;
    private Status status;
    private final double price;

    public Room(String id, Status status, double price) {
        this.id = id;
        this.status = status;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }
}
