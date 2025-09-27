package br.ifsp.demo.domain;

import java.util.UUID;

public class Room {

    private String id;
    private Status status;
    private double price;

    public Room(Status status, double price) {
        this.id = UUID.randomUUID().toString();
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
