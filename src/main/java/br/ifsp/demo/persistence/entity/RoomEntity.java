package br.ifsp.demo.persistence.entity;

import br.ifsp.demo.domain.model.RoomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class RoomEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status;

    @Column(name = "price", nullable = false)
    private double price;
}
