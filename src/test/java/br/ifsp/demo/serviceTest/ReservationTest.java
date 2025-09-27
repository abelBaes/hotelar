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
import java.time.LocalDateTime;
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
                        LocalDateTime.of(2025, 10, 6, 14, 0),
                        LocalDateTime.of(2025, 10, 7, 11, 0)
                ),
                Arguments.of(
                        new Room("102", Status.AVAILABLE, 250.0),
                        new Guest("Pedro", 30),
                        LocalDateTime.of(2025, 10, 15, 14, 0),
                        LocalDateTime.of(2025, 10, 16, 11, 0)
                ), Arguments.of(
                        new Room("102", Status.AVAILABLE, 250.0),
                        new Guest("Pedro", 30),
                        LocalDateTime.of(2025, 10, 16, 12, 0),
                        LocalDateTime.of(2025, 10, 18, 11, 0)
                )
        );
    }


    static Stream<Arguments> reservationConflictProvider() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0),
                        LocalDateTime.of(2025, 11, 12, 10, 0),
                        LocalDateTime.of(2025, 11, 14, 11, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0),
                        LocalDateTime.of(2025, 11, 9, 23, 59),
                        LocalDateTime.of(2025, 11, 11, 9, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0),
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 16, 0, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0),
                        LocalDateTime.of(2025, 11, 11, 8, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 11, 10, 14, 0),
                        LocalDateTime.of(2025, 11, 15, 12, 0),
                        LocalDateTime.of(2025, 11, 14, 0, 0),
                        LocalDateTime.of(2025, 11, 18, 12, 0)
                )
        );
    }


    @ParameterizedTest
    @MethodSource("reservationProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldCreateReservation(Room room, Guest guest, LocalDateTime checkIn, LocalDateTime checkOut) {
        Reservation obtained = sut.createReservation(room, guest, checkIn, checkOut);

        assertThat(obtained).isNotNull();
        assertThat(sut.getAllReservations()).isNotEmpty();
        assertThat(obtained.getGuest().getName()).isEqualTo(guest.getName());
        assertThat(obtained.getRoom().getId()).isEqualTo(room.getId());
        assertThat(obtained.getRoom().getStatus()).isEqualTo(Status.RESERVED);
    }

    @ParameterizedTest(name = "[{index}] Overlap: {0} - {1} with {2} - {3}")
    @Tag("UnitTest")
    @MethodSource(value = "reservationConflictProvider")
    void shouldNotAllowOverlappingReservationsForSameRoom(LocalDateTime firstCheckIn, LocalDateTime firstCheckOut, LocalDateTime secondCheckInOverLaped, LocalDateTime secondCheckOutOverLaped) {
        Room room102 = new Room("102", Status.AVAILABLE, 200.0);
        Guest guest1 = new Guest("Marcos", 35);
        Guest guest2 = new Guest("Fernanda", 29);

        sut.createReservation(room102, guest1, firstCheckIn, firstCheckOut);

        assertThatThrownBy(() ->
                sut.createReservation(room102, guest2, secondCheckInOverLaped, secondCheckOutOverLaped))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

}
