package br.ifsp.demo.serviceTest;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {

    private ReservationService sut;

    @BeforeEach
    void setup() {
        sut = new ReservationService();
    }

    static Stream<Arguments> reservationProvider() {
        return Stream.of(
                Arguments.of(
                        new Room("101", Status.AVAILABLE, 250.0),
                        new Guest("Maria", 30),
                        LocalDate.of(2025, 10, 6),
                        LocalDate.of(2025, 10, 7)
                ),
                Arguments.of(
                        new Room("102", Status.AVAILABLE, 250.0),
                        new Guest("Pedro", 30),
                        LocalDate.of(2025, 10, 15),
                        LocalDate.of(2025, 10, 16)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("reservationProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldCreateReservation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        Reservation obtained = sut.createReservation(room, guest, checkIn, checkOut);

        assertThat(obtained).isNotNull();
        assertThat(sut.getAllReservations()).isNotEmpty();
        assertThat(obtained.getGuest().getName()).isEqualTo(guest.getName());
        assertThat(obtained.getRoom().getId()).isEqualTo(room.getId());
        assertThat(obtained.getRoom().getStatus()).isEqualTo(Status.RESERVED);
    }

    @Test
    void shouldNotAllowOverlappingReservationsForSameRoom() {
        Room room102 = new Room("102", Status.AVAILABLE, 200.0);
        Guest guest1 = new Guest("Marcos", 35);
        Guest guest2 = new Guest("Fernanda", 29);

        LocalDate firstCheckIn = LocalDate.of(2025, 11, 10);
        LocalDate firstCheckOut = LocalDate.of(2025, 11, 15);

        sut.createReservation(room102, guest1, firstCheckIn, firstCheckOut);

        LocalDate overlappingCheckIn = LocalDate.of(2025, 11, 12);
        LocalDate overlappingCheckOut = LocalDate.of(2025, 11, 14);

        assertThatThrownBy(() ->
                sut.createReservation(room102, guest2, overlappingCheckIn, overlappingCheckOut))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

}
