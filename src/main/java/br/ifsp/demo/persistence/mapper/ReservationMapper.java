package br.ifsp.demo.persistence.mapper;

import br.ifsp.demo.domain.model.*;
import br.ifsp.demo.persistence.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationEntity toEntity(Reservation domain) {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(domain.getId());
        entity.setRoom(RoomMapper.toEntity(domain.getRoom()));
        entity.setGuest(GuestMapper.toEntity(domain.getGuest()));
        entity.setStayPeriod(toEmbeddable(domain.getStayPeriod()));
        entity.setReservationStatus(domain.getReservationStatus());
        entity.setExtraServices(toEmbeddableList(domain.getExtraServices()));
        return entity;
    }

    public static Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                RoomMapper.toDomain(entity.getRoom()),
                GuestMapper.toDomain(entity.getGuest()),
                toDomain(entity.getStayPeriod()),
                entity.getReservationStatus()
        );
    }

    private static StayPeriodEmbeddable toEmbeddable(StayPeriod domain) {
        StayPeriodEmbeddable emb = new StayPeriodEmbeddable();
        emb.setCheckin(domain.getCheckin());
        emb.setCheckout(domain.getCheckout());
        return emb;
    }

    private static StayPeriod toDomain(StayPeriodEmbeddable emb) {
        return new StayPeriod(emb.getCheckin(), emb.getCheckout());
    }

    private static List<ExtraServiceEmbeddable> toEmbeddableList(List<ExtraService> domainList) {
        return domainList.stream()
                .map(service -> {
                    ExtraServiceEmbeddable emb = new ExtraServiceEmbeddable();
                    emb.setDescription(service.getDescription());
                    emb.setValue(service.getValue());
                    return emb;
                })
                .collect(Collectors.toList());
    }

    private static List<ExtraService> toDomainList(List<ExtraServiceEmbeddable> embList) {
        return embList.stream()
                .map(emb -> new ExtraService(emb.getDescription(), emb.getValue()))
                .collect(Collectors.toList());
    }
}
