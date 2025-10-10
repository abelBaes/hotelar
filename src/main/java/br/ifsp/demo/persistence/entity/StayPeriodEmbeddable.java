package br.ifsp.demo.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class StayPeriodEmbeddable {

    private LocalDateTime checkin;
    private LocalDateTime checkout;
}
