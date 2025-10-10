package br.ifsp.demo.persistence.mapper;

import br.ifsp.demo.domain.model.Guest;
import br.ifsp.demo.persistence.entity.GuestEntity;

public class GuestMapper {

    public static GuestEntity toEntity(Guest domain) {
        GuestEntity entity = new GuestEntity();
        entity.setCpf(domain.getCpf());
        entity.setName(domain.getName());
        entity.setAge(domain.getAge());
        entity.setVip(domain.isVip());
        return entity;
    }

    public static Guest toDomain(GuestEntity entity) {
        return new Guest(
                entity.getName(),
                entity.getAge(),
                entity.getCpf(),
                entity.isVip()
        );
    }
}
