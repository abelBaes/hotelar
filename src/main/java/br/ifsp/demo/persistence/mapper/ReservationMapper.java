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
        entity.setGuest(guestEmbeddable(domain.getGuest()));
        entity.setStayPeriod(stayPeriodEmbeddable(domain.getStayPeriod()));
        entity.setReservationStatus(domain.getReservationStatus());
        entity.setExtraServices(toEmbeddableList(domain.getExtraServices()));
        return entity;
    }

    public static Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                RoomMapper.toDomain(entity.getRoom()),
                guestDomain(entity.getGuest()),
                stayPeriodDomain(entity.getStayPeriod()),
                entity.getReservationStatus()
        );
    }

    private static StayPeriodEmbeddable stayPeriodEmbeddable(StayPeriod domain) {
        StayPeriodEmbeddable emb = new StayPeriodEmbeddable();
        emb.setCheckin(domain.getCheckin());
        emb.setCheckout(domain.getCheckout());
        return emb;
    }

    private static GuestEmbeddable guestEmbeddable(Guest domain) {
        GuestEmbeddable emb = new GuestEmbeddable();
        emb.setCpf(domain.getCpf());
        emb.setName(domain.getName());
        emb.setAge(domain.getAge());
        emb.setVip(domain.isVip());

        return emb;
    }

    private static StayPeriod stayPeriodDomain(StayPeriodEmbeddable emb) {
        return new StayPeriod(emb.getCheckin(), emb.getCheckout());
    }

    private static Guest guestDomain(GuestEmbeddable emb) {
        return new Guest(emb.getName(), emb.getAge(), emb.getCpf(), emb.isVip());
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
