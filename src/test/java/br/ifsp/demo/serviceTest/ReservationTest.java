package br.ifsp.demo.serviceTest;

import br.ifsp.demo.domain.Reservation;
import br.ifsp.demo.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationTest {

    private ReservationService sut;

    @BeforeEach
    void setup() {
        sut = new ReservationService();
    }

    @Test
    void shouldCreateReservation() {
        Reservation obtained = sut.createReservation();

        assertThat(obtained).isNotNull();
    }
}
