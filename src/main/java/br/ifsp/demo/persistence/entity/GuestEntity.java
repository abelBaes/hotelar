package br.ifsp.demo.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guests")
@Getter
@Setter
public class GuestEntity {

    @Id
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "is_vip")
    private boolean isVip;
}
