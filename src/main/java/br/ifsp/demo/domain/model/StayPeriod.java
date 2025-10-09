package br.ifsp.demo.domain.model;

import java.time.LocalDateTime;

public class StayPeriod{
    private final LocalDateTime checkin;
    private final LocalDateTime checkout;

    public StayPeriod(LocalDateTime checkin, LocalDateTime checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public LocalDateTime getCheckin() {
        return checkin;
    }

    public LocalDateTime getCheckout() {
        return checkout;
    }

    @Override
    public String toString() {
        return "StayPeriod{" +
                "checkin=" + checkin +
                ", checkout=" + checkout +
                '}';
    }
}
