package br.ifsp.demo.serviceTest;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.service.ReservationService;
import net.bytebuddy.asm.MemberSubstitution;
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

    static Stream<Arguments> invalidDatesProvider() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2025, 10, 10, 14, 0),
                        LocalDateTime.of(2025, 10, 10, 14, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 10, 12, 14, 0),
                        LocalDateTime.of(2025, 10, 11, 11, 0)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 10, 10, 14, 0),
                        LocalDateTime.of(2025, 10, 10, 13, 59)
                ),
                Arguments.of(
                        LocalDateTime.of(2025, 10, 10, 23, 59),
                        LocalDateTime.of(2025, 10, 10, 0, 0)
                )
        );
    }

    static Stream<Arguments> pastDatesProvider() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1)
                ),
                Arguments.of(
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(5)
                ),
                Arguments.of(
                        LocalDateTime.now().minusMinutes(1),
                        LocalDateTime.now().plusDays(2)
                ),
                Arguments.of(
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1)
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
    @Tag("TDD")
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

    @ParameterizedTest(name = "[{index}] checkIn={0}, checkOut={1} - INVALID")
    @MethodSource("invalidDatesProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWhenCheckInIsAfterOrEqualToCheckOut(LocalDateTime invalidCheckIn, LocalDateTime invalidCheckOut) {
        Room room = new Room("201", Status.AVAILABLE, 150.0);
        Guest guest = new Guest("João", 25);

        assertThatThrownBy(() ->
                sut.createReservation(room, guest, invalidCheckIn, invalidCheckOut)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-in date must be before check-out date");
    }

    @ParameterizedTest(name = "[{index}] Past reservation invalid → checkIn={0}, checkOut={1}")
    @MethodSource(value = "pastDatesProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWithPastDates(LocalDateTime pastCheckIn, LocalDateTime pastCheckOut) {
        Room room = new Room("301", Status.AVAILABLE, 180.0);
        Guest guest = new Guest("Clara", 27);

        assertThatThrownBy(() ->
                sut.createReservation(room, guest, pastCheckIn, pastCheckOut)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reservation dates must be in the future");
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationForNonExistingRoom() {
        Guest guest = new Guest("Lucas", 27);
        LocalDateTime checkIn = LocalDateTime.of(2025, 11, 20, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2025, 11, 22, 11, 0);

        assertThatThrownBy(() ->
                sut.createReservation(null, guest, checkIn, checkOut)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room does not exist");
    }


    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationForRoomUnderMaintenance() {
        Room roomInMaintenance = new Room("401", Status.UNDER_MAINTENANCE, 300.0);
        Guest guest = new Guest("Julia", 32);
        LocalDateTime checkIn = LocalDateTime.of(2025, 12, 1, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2025, 12, 3, 11, 0);

        assertThatThrownBy(() ->
                sut.createReservation(roomInMaintenance, guest, checkIn, checkOut)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Room is under maintenance");
    }



}
