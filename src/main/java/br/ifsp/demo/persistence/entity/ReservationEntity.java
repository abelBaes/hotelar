package br.ifsp.demo.persistence.entity;

import br.ifsp.demo.domain.model.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class ReservationEntity {

    @Id
    private String id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "guest_id")
    private GuestEntity guest;

    @Embedded
    private StayPeriodEmbeddable stayPeriod;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ElementCollection
    @CollectionTable(
            name = "reservation_extra_services",
            joinColumns = @JoinColumn(name = "reservation_id")
    )
    private List<ExtraServiceEmbeddable> extraServices = new ArrayList<>();
}
