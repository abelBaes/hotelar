package br.ifsp.demo.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ExtraServiceEmbeddable {

    private String description;
    private double value;
}
