package br.ifsp.demo.dto.mapper;

import br.ifsp.demo.domain.model.Guest;
import br.ifsp.demo.domain.model.Reservation;
import br.ifsp.demo.domain.model.StayPeriod;
import br.ifsp.demo.domain.model.ExtraService;
import br.ifsp.demo.dto.request.CreateReservationRequest;
import br.ifsp.demo.dto.request.GuestDTO;
import br.ifsp.demo.dto.response.ReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoMapper {

    public Guest toGuest(CreateReservationRequest request) {
        GuestDTO dto = request.guest();
        return new Guest(dto.name(), dto.age(), dto.cpf(), dto.isVip());
    }

    public StayPeriod toStayPeriod(CreateReservationRequest request) {
        return new StayPeriod(request.checkin(), request.checkout());
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getGuest().getName(),
                reservation.getStayPeriod().getCheckin(),
                reservation.getStayPeriod().getCheckout(),
                reservation.getReservationStatus().name(),
                reservation.getExtraServices().stream()
                        .map(ExtraService::getDescription)
                        .toList()
        );
    }
}
