package br.ifsp.demo.persistence.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestEmbeddable{

    private String cpf;
    private String name;
    private Integer age;
    private boolean isVip;
}
