package br.ifsp.demo.serviceTest;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repository.FakeReservationRepository;
import br.ifsp.demo.service.ReservationService;
import net.bytebuddy.asm.MemberSubstitution;
import org.codehaus.plexus.util.cli.Arg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import javax.enterprise.inject.Stereotype;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {

    private ReservationService sut;

    @BeforeEach
    void setup() {
        sut = new ReservationService(new FakeReservationRepository());
    }

    static Stream<Arguments> reservationProvider() {
        return Stream.of(
                Arguments.of(
                        new Room("101", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Maria", 30, "78609833038"),
                        new StayPeriod(LocalDateTime.of(2025, 10, 6, 14, 0),
                                LocalDateTime.of(2025, 10, 7, 11, 0))
                ),
                Arguments.of(
                        new Room("102", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Pedro", 30, "75352394042"),
                        new StayPeriod(LocalDateTime.of(2025, 10, 15, 14, 0),
                                LocalDateTime.of(2025, 10, 16, 11, 0))
                ), Arguments.of(
                        new Room("102", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Pedro", 30, "00356457095"),
                        new StayPeriod(LocalDateTime.of(2025, 10, 16, 12, 0),
                                LocalDateTime.of(2025, 10, 18, 11, 0))
                )
        );
    }


    static Stream<Arguments> reservationConflictProvider() {
        StayPeriod basePeriod = new StayPeriod(
                LocalDateTime.of(2025, 11, 10, 14, 0),
                LocalDateTime.of(2025, 11, 15, 12, 0)
        );

        return Stream.of(
                Arguments.of(
                        basePeriod,
                        new StayPeriod(LocalDateTime.of(2025, 11, 12, 10, 0),
                                LocalDateTime.of(2025, 11, 14, 11, 0))
                ),
                Arguments.of(
                        basePeriod,
                        new StayPeriod(LocalDateTime.of(2025, 11, 9, 23, 59),
                                LocalDateTime.of(2025, 11, 11, 9, 0))
                ),
                Arguments.of(
                        basePeriod,
                        new StayPeriod(LocalDateTime.of(2025, 11, 10, 14, 0),
                                LocalDateTime.of(2025, 11, 16, 0, 0))
                ),
                Arguments.of(
                        basePeriod,
                        new StayPeriod(LocalDateTime.of(2025, 11, 11, 8, 0),
                                LocalDateTime.of(2025, 11, 15, 12, 0))
                ),
                Arguments.of(
                        basePeriod,
                        new StayPeriod(LocalDateTime.of(2025, 11, 14, 0, 0),
                                LocalDateTime.of(2025, 11, 18, 12, 0))
                )
        );
    }

    static Stream<Arguments> invalidDatesProvider() {
        return Stream.of(
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.of(2025, 10, 10, 14, 0),
                                LocalDateTime.of(2025, 10, 10, 14, 0)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.of(2025, 10, 12, 14, 0),
                                LocalDateTime.of(2025, 10, 11, 11, 0)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.of(2025, 10, 10, 14, 0),
                                LocalDateTime.of(2025, 10, 10, 13, 59)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.of(2025, 10, 10, 23, 59),
                                LocalDateTime.of(2025, 10, 10, 0, 0)
                        )
                )
        );
    }

    static Stream<Arguments> pastDatesProvider() {
        return Stream.of(
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.now().minusDays(1),
                                LocalDateTime.now().plusDays(1)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.now().minusHours(1),
                                LocalDateTime.now().plusHours(5)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.now().minusMinutes(1),
                                LocalDateTime.now().plusDays(2)
                        )
                ),
                Arguments.of(
                        new StayPeriod(
                                LocalDateTime.now().minusDays(2),
                                LocalDateTime.now().minusDays(1)
                        )
                )
        );
    }

    static Stream<Arguments> invalidGuestProvider() {
        return Stream.of(
                // Invalid CPF, rest valid
                Arguments.of("Maria", 25, ""),
                Arguments.of("João", 30, null),

                // Invalid name, rest valid
                Arguments.of("", 25, "66158381098"),
                Arguments.of(null, 25, "83333159090"),

                // Invalid age, rest valid
                Arguments.of("Pedro", null, "22038920052")
        );
    }

    @ParameterizedTest
    @MethodSource("reservationProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldCreateReservation(Room room, Guest guest, StayPeriod stayPeriod) {
        Reservation obtained = sut.createReservation(room, guest, stayPeriod);

        assertThat(obtained).isNotNull();
        assertThat(sut.getAllReservations()).isNotEmpty();
        assertThat(obtained.getGuest().getName()).isEqualTo(guest.getName());
        assertThat(obtained.getRoom().getId()).isEqualTo(room.getId());
        assertThat(obtained.getReservationStatus()).isEqualTo(ReservationStatus.ACTIVE);
    }

    @ParameterizedTest(name = "[{index}] Overlap: {0} - {1} with {2} - {3}")
    @Tag("UnitTest")
    @Tag("TDD")
    @MethodSource(value = "reservationConflictProvider")
    void shouldNotAllowOverlappingReservationsForSameRoom(StayPeriod firsStayPeriod, StayPeriod secondStayPeriod) {
        Room room102 = new Room("102", RoomStatus.AVAILABLE, 200.0);
        Guest guest1 = new Guest("Marcos", 35, "30639680054");
        Guest guest2 = new Guest("Fernanda", 29, "15495812018");

        sut.createReservation(room102, guest1, firsStayPeriod);

        assertThatThrownBy(() ->
                sut.createReservation(room102, guest2, secondStayPeriod))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

    @ParameterizedTest(name = "[{index}] {0} - INVALID")
    @MethodSource("invalidDatesProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWhenCheckInIsAfterOrEqualToCheckOut(StayPeriod invalidStayPeriod) {
        Room room = new Room("201", RoomStatus.AVAILABLE, 150.0);
        Guest guest = new Guest("João", 25, "85856073002");

        assertThatThrownBy(() ->
                sut.createReservation(room, guest, invalidStayPeriod)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-in date must be before check-out date");
    }

    @ParameterizedTest(name = "[{index}] Past reservation invalid → {0}")
    @MethodSource(value = "pastDatesProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWithPastDates(StayPeriod pastStayPeriod) {
        Room room = new Room("301", RoomStatus.AVAILABLE,180.0);
        Guest guest = new Guest("Clara", 27, "41237267048");

        assertThatThrownBy(() ->
                sut.createReservation(room, guest, pastStayPeriod)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reservation dates must be in the future");
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationForNonExistingRoom() {
        Guest guest = new Guest("Lucas", 27, "25971503057");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 11, 20, 14, 0),
                LocalDateTime.of(2025, 11, 22, 11, 0));

        assertThatThrownBy(() ->
                sut.createReservation(null, guest, stayPeriod)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Room does not exist");
    }


    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationForRoomUnderMaintenance() {
        Room roomInMaintenance = new Room("401", RoomStatus.UNDER_MAINTENANCE, 300.0);
        Guest guest = new Guest("Julia", 32, "61708839011");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 1, 14, 0),
                LocalDateTime.of(2025, 12, 3, 11, 0));

        assertThatThrownBy(() ->
                sut.createReservation(roomInMaintenance, guest, stayPeriod)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Room is under maintenance");
    }

    @ParameterizedTest(name = "[{index}] Guest age={0} - Allowed={1}")
    @CsvSource({
            "17, false",
            "18, true",
            "19, true"})
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldValidateGuestAgeForReservation(int age, boolean shouldSucceed) {
        Room room = new Room("301", RoomStatus.AVAILABLE, 180.0);
        Guest guest = new Guest("Test Guest", age, "19663936010");

        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 12, 14, 0),
                    LocalDateTime.of(2025, 12, 13, 11, 0));

        if (shouldSucceed) {
            Reservation reservation = sut.createReservation(room, guest, stayPeriod);
            assertThat(reservation).isNotNull();
            assertThat(reservation.getGuest().getAge()).isGreaterThanOrEqualTo(18);
        } else {
            assertThatThrownBy(() -> sut.createReservation(room, guest, stayPeriod))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least 18 years old");
        }
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWithoutGuest() {
        Room room = new Room("301", RoomStatus.AVAILABLE, 200.0);

        assertThatThrownBy(() ->
                sut.createReservation(room, null,
                        new StayPeriod(LocalDateTime.of(2025, 12, 1, 14, 0),
                        LocalDateTime.of(2025, 12, 5, 11, 0)))
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Guest must not be null");
    }

    @ParameterizedTest (name = "[{index}] invalidGuest: name={0}, age={1}, cpf={2}")
    @MethodSource(value = "invalidGuestProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotAllowReservationWithInvalidGuestData(String name, Integer age, String cpf) {
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest invalidGuest = new Guest(name, age, cpf);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 10, 6, 14, 0),
                LocalDateTime.of(2025, 10, 8, 11, 0));

        assertThatThrownBy(() ->
                sut.createReservation(room, invalidGuest, stayPeriod)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Guest must provide valid CPF, name and age");
    }

    @Test
    @DisplayName("Should be possible to add a extra service in an active Reservation")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldBePossibleToAddAExtraServiceInAnActiveReservation(){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 10, 6, 14, 0),
                LocalDateTime.of(2025, 10, 8, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        ExtraService extraService = new ExtraService("Laundry", 30.0);
        assertThat(sut.addExtraService(reservation.getId(), extraService).getExtraServices())
                .isNotEmpty()
                .contains(extraService)
                .doesNotContainNull();
    }





}
