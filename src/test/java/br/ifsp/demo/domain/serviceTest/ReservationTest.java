package br.ifsp.demo.domain.serviceTest;

import br.ifsp.demo.domain.model.*;
import br.ifsp.demo.persistence.repository.FakeReservationRepository;
import br.ifsp.demo.persistence.repository.ReservationRepository;
import br.ifsp.demo.domain.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {

    ReservationRepository fakeRepository;
    ReservationService sut;

    @BeforeEach
    void setup() {
        fakeRepository = new FakeReservationRepository();
        sut = new ReservationService(fakeRepository);
    }

    static Stream<Arguments> reservationProvider() {
        return Stream.of(
                Arguments.of(
                        new Room("101", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Maria", 30, "78609833038"),
                        new StayPeriod(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                                LocalDateTime.now().plusDays(2).withHour(11).withMinute(0))
                ),
                Arguments.of(
                        new Room("102", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Pedro", 30, "75352394042"),
                        new StayPeriod(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
                                LocalDateTime.now().plusDays(3).withHour(11).withMinute(0))
                ), Arguments.of(
                        new Room("102", RoomStatus.AVAILABLE, 250.0),
                        new Guest("Pedro", 30, "00356457095"),
                        new StayPeriod(LocalDateTime.now().plusDays(3).withHour(12).withMinute(0),
                                LocalDateTime.now().plusDays(5).withHour(11).withMinute(0))
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

    static Stream<Arguments> changeReservationPeriodProvider(){
        return Stream.of(
                Arguments.of(new StayPeriod(LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 10, 11, 0))),
                Arguments.of(new StayPeriod(LocalDateTime.of(2025, 9, 1, 0, 0),
                        LocalDateTime.of(2025, 10, 10, 0, 0)))
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
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 15, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        ExtraService extraService = new ExtraService("Laundry", 30.0);
        assertThat(sut.addExtraService(reservation.getId(), extraService).getExtraServices())
                .isNotEmpty()
                .contains(extraService)
                .doesNotContainNull();
    }

    @Test
    @DisplayName("Should not be possible to add a extra service in an inexistent Reservation")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToAddAExtraServiceInAnInexistentReservation(){
        ExtraService extraService = new ExtraService("Laundry", 30.0);
        assertThatThrownBy(() -> sut.addExtraService("H-20251005223045384920",extraService))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Reservation not found for id: H-20251005223045384920");
    }

    @Test
    @DisplayName("Should apply 15% discount for VIP guest during checkout")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldApplyFifteenPercentDiscountForVipGuestDuringCheckout() {
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest vipGuest = new Guest("VIP Guest", 30, "78609833038", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 15, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 15, 11, 0));

        // 2 nights * 250.0 = 500.0, com desconto de 15% = 425.0
        assertThat(totalAmount).isEqualTo(425.0);
    }

    @Test
    @DisplayName("Should not apply discount for non-VIP guest during checkout")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotApplyDiscountForNonVipGuestDuringCheckout() {
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest regularGuest = new Guest("Regular Guest", 30, "78609833038", false);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 15, 11, 0));
        Reservation reservation = sut.createReservation(room, regularGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 15, 11, 0));

        // 2 nights * 250.0 = 500.0, sem desconto = 500.0
        assertThat(totalAmount).isEqualTo(500.0);
    }

    @Test
    @DisplayName("Should throw exception when checkout with non-existent reservation ID")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldThrowExceptionWhenCheckoutWithNonExistentReservationId() {
        String nonExistentId = "H-20251005223045384920";
        assertThatThrownBy(() -> sut.checkout(nonExistentId, LocalDateTime.of(2025, 12, 15, 11, 0)))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Reservation not found for id: H-20251005223045384920");
    }

    @Test
    @DisplayName("Should throw exception when checkout with null reservation ID")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldThrowExceptionWhenCheckoutWithNullReservationId() {
        assertThatThrownBy(() -> sut.checkout(null, LocalDateTime.of(2025, 12, 15, 11, 0)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Reservation ID must not be null");
    }

    @Test
    @DisplayName("Should throw exception when checkout with empty reservation ID")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldThrowExceptionWhenCheckoutWithEmptyReservationId() {
        assertThatThrownBy(() -> sut.checkout("", LocalDateTime.of(2025, 12, 15, 11, 0)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid Reservation ID format");
    }

    @Test
    @DisplayName("Should apply correct VIP discount for single night stay")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldApplyCorrectVipDiscountForSingleNightStay() {
        Room room = new Room("102", RoomStatus.AVAILABLE, 300.0);
        Guest vipGuest = new Guest("VIP Guest", 35, "12345678901", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 14, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 14, 11, 0));

        // 1 night * 300.0 = 300.0, com desconto de 15% = 255.0
        assertThat(totalAmount).isEqualTo(255.0);
    }

    @Test
    @DisplayName("Should apply correct VIP discount for long stay (7 nights)")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldApplyCorrectVipDiscountForLongStay() {
        Room room = new Room("103", RoomStatus.AVAILABLE, 200.0);
        Guest vipGuest = new Guest("VIP Guest", 40, "98765432100", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 20, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 20, 11, 0));

        // 7 nights * 200.0 = 1400.0, com desconto de 15% = 1190.0
        assertThat(totalAmount).isEqualTo(1190.0);
    }

    @Test
    @DisplayName("Should apply VIP discount with extra services")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldApplyVipDiscountWithExtraServices() {
        Room room = new Room("104", RoomStatus.AVAILABLE, 150.0);
        Guest vipGuest = new Guest("VIP Guest", 25, "11122233344", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 15, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        ExtraService wifi = new ExtraService("WiFi", 50.0);
        ExtraService breakfast = new ExtraService("Breakfast", 30.0);
        sut.addExtraService(reservation.getId(), wifi);
        sut.addExtraService(reservation.getId(), breakfast);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 15, 11, 0));

        // (2 nights * 150.0 + 50.0 + 30.0) * 0.85 = (300.0 + 80.0) * 0.85 = 323.0
        assertThat(totalAmount).isEqualTo(323.0);
    }

    @Test
    @DisplayName("Should not apply discount for non-VIP guest with extra services")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotApplyDiscountForNonVipGuestWithExtraServices() {
        Room room = new Room("105", RoomStatus.AVAILABLE, 180.0);
        Guest regularGuest = new Guest("Regular Guest", 28, "55566677788", false);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 16, 11, 0));
        Reservation reservation = sut.createReservation(room, regularGuest, stayPeriod);

        ExtraService spa = new ExtraService("Spa", 100.0);
        sut.addExtraService(reservation.getId(), spa);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 16, 11, 0));

        // 3 nights * 180.0 + 100.0 = 540.0 + 100.0 = 640.0 (sem desconto)
        assertThat(totalAmount).isEqualTo(640.0);
    }

    @Test
    @DisplayName("Should handle zero-price room correctly for VIP guest")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldHandleZeroPriceRoomCorrectlyForVipGuest() {
        Room room = new Room("106", RoomStatus.AVAILABLE, 0.0);
        Guest vipGuest = new Guest("VIP Guest", 30, "99988877766", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 14, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 14, 11, 0));

        // 1 night * 0.0 = 0.0, com desconto de 15% = 0.0
        assertThat(totalAmount).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should handle high-price room correctly for VIP guest")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldHandleHighPriceRoomCorrectlyForVipGuest() {
        Room room = new Room("107", RoomStatus.AVAILABLE, 10000.0);
        Guest vipGuest = new Guest("VIP Guest", 45, "44455566677", true);
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 13, 14, 0),
                LocalDateTime.of(2025, 12, 14, 11, 0));
        Reservation reservation = sut.createReservation(room, vipGuest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 14, 11, 0));

        // 1 night * 10000.0 = 10000.0, com desconto de 15% = 8500.0
        assertThat(totalAmount).isEqualTo(8500.0);
    }

    @Test
    @DisplayName("Should verify Guest isVip method returns correct value for VIP guest")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldVerifyGuestIsVipMethodReturnsCorrectValueForVipGuest() {
        Guest vipGuest = new Guest("VIP Guest", 30, "12345678901", true);
        assertThat(vipGuest.isVip()).isTrue();
    }

    @Test
    @DisplayName("Should verify Guest isVip method returns correct value for non-VIP guest")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldVerifyGuestIsVipMethodReturnsCorrectValueForNonVipGuest() {
        Guest regularGuest = new Guest("Regular Guest", 30, "12345678901", false);
        assertThat(regularGuest.isVip()).isFalse();
    }

    @Test
    @DisplayName("Should verify Guest isVip method returns false when using default constructor")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldVerifyGuestIsVipMethodReturnsFalseWhenUsingDefaultConstructor() {
        Guest guest = new Guest("Guest", 30, "12345678901");
        assertThat(guest.isVip()).isFalse();
    }

    @DisplayName("Should be possible to change a Active Reservation Stay Period")
    @ParameterizedTest(name = "[{index}] - New Stay Period of {0}")
    @Tag("UnitTest")
    @Tag("TDD")
    @MethodSource("changeReservationPeriodProvider")
    void shouldBePossibleToChangeAnStayPeriodOfAActiveReservation(StayPeriod newStayPeriod){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 10, 10, 0, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);
        Reservation obtained = sut.updateStayPeriod(reservation.getId(), newStayPeriod);

        assertThat(obtained.getStayPeriod().getCheckin()).isEqualTo(newStayPeriod.getCheckin());
        assertThat(obtained.getStayPeriod().getCheckout()).isEqualTo(newStayPeriod.getCheckout());
    }

    @DisplayName("Should not be possible to change a Active Reservation with invalid Stay Period")
    @ParameterizedTest(name = "[{index}] - New Stay Period of {0}")
    @Tag("UnitTest")
    @Tag("TDD")
    @MethodSource("invalidDatesProvider")
    void shouldNotBePossibleToChangeAActiveReservationWithInvalidStayPeriod(StayPeriod invalidStayPeriod){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 10, 10, 0, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);
        assertThatThrownBy(() -> sut.updateStayPeriod(reservation.getId(), invalidStayPeriod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-in date must be before check-out date");
    }

    @Test
    @DisplayName("Should not be possible to change a non existent Reservation.")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToChangeANonExistentReservation(){
        String nonExistentReservationId = "H-20251005223045384920";
        StayPeriod newStayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 9, 10, 0, 0));

        assertThatThrownBy(() -> sut.updateStayPeriod(nonExistentReservationId, newStayPeriod))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Reservation not found for id: H-20251005223045384920");
    }

    @ParameterizedTest(name = "Should not be possible to change a non Active Reservation.")
    @Tag("UnitTest")
    @Tag("TDD")
    @CsvSource({"FINALIZED", "CANCELED"})
    void shouldNotBePossibleToChangeANonActiveReservation(ReservationStatus status){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 10, 10, 0, 0));
        String reservationId = "H-20251005223045384920";

        StayPeriod newStayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 9, 10, 0, 0));

        fakeRepository.save(new Reservation(reservationId, room, guest, stayPeriod, status));

        assertThatThrownBy(() -> sut.updateStayPeriod(reservationId, newStayPeriod))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Reservation must be Active");
    }

    @ParameterizedTest(name = "Should not be possible to change the Stay Period to a non avaible one.")
    @MethodSource("reservationConflictProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToChangeTheStayPeriodToANonAvaibleOne(StayPeriod alreadyExistentStayPeriod, StayPeriod newStayPeriod){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest1 = new Guest("Lucas", 38, "78609833038");
        Guest guest2 = new Guest("Fernanda", 29, "15495812018");
        StayPeriod avaybleStayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 10, 14, 0),
                LocalDateTime.of(2025, 12, 15, 12, 0));

        sut.createReservation(room,guest1,alreadyExistentStayPeriod);
        Reservation reservationToBeChanged = sut.createReservation(room,guest2,avaybleStayPeriod);

        assertThatThrownBy(() ->
                sut.updateStayPeriod(reservationToBeChanged.getId(), newStayPeriod))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

    static Stream<Arguments> invalidCheckoutStatusProvider() {
        return Stream.of(
                Arguments.of(ReservationStatus.FINALIZED),
                Arguments.of(ReservationStatus.CANCELED)
        );
    }

    static Stream<Arguments> invalidCheckoutDateProvider() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2024, 12, 10, 11, 0)), // Past date
                Arguments.of(LocalDateTime.of(2024, 12, 14, 10, 0))  // Past date
        );
    }

    @Test
    @DisplayName("Should not be possible to checkout a finalized reservation")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToCheckoutFinalizedReservation() {
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 18, 11, 0));

        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 17, 11, 0));

        assertThatThrownBy(() -> sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 17, 11, 0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only active reservations can be checked out");
    }

    @Test
    @DisplayName("Should finalize reservation after successful checkout")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldFinalizeReservationAfterSuccessfulCheckout() {
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 18, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        double totalAmount = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 17, 11, 0));

        assertThat(totalAmount).isEqualTo(250.0); // 1 night * 250.0

        Reservation updatedReservation = sut.getAllReservations().stream()
                .filter(r -> r.getId().equals(reservation.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(updatedReservation.getReservationStatus()).isEqualTo(ReservationStatus.FINALIZED);
    }

    @Test
    @DisplayName("Should not be possible to checkout the same reservation twice")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotBePossibleToCheckoutTheSameReservationTwice() {
        Room room = new Room("102", RoomStatus.AVAILABLE, 200.0);
        Guest guest = new Guest("Maria", 30, "12345678901");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 17, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        double firstCheckout = sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 17, 11, 0));
        assertThat(firstCheckout).isEqualTo(200.0);

        assertThatThrownBy(() -> sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 16, 11, 0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only active reservations can be checked out");
    }

    @Test
    @DisplayName("Should not be possible to add extra services to finalized reservation")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotBePossibleToAddExtraServicesToFinalizedReservation() {
        Room room = new Room("103", RoomStatus.AVAILABLE, 300.0);
        Guest guest = new Guest("João", 35, "98765432100");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 17, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        sut.checkout(reservation.getId(), LocalDateTime.of(2025, 12, 16, 11, 0));

        ExtraService extraService = new ExtraService("Room Service", 50.0);
        assertThatThrownBy(() -> sut.addExtraService(reservation.getId(), extraService))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add services to finalized or canceled reservations");
    }

    @DisplayName("Should not be possible to checkout with date in the past")
    @ParameterizedTest(name = "[{index}] Past date: {0}")
    @MethodSource("invalidCheckoutDateProvider")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToCheckoutWithDateInThePast(LocalDateTime pastDate) {
        Room room = new Room("105", RoomStatus.AVAILABLE, 200.0);
        Guest guest = new Guest("Carlos", 32, "12345678901");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 18, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        assertThatThrownBy(() -> sut.checkout(reservation.getId(), pastDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Checkout date cannot be in the past");
    }

    @Test
    @DisplayName("Should allow early checkout (before scheduled checkout date)")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldAllowEarlyCheckout() {
        Room room = new Room("106", RoomStatus.AVAILABLE, 300.0);
        Guest guest = new Guest("Early Guest", 28, "98765432100");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 20, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        // Early checkout (2 days before scheduled)
        LocalDateTime earlyCheckoutDate = LocalDateTime.of(2025, 12, 18, 10, 0);
        double totalAmount = sut.checkout(reservation.getId(), earlyCheckoutDate);

        assertThat(totalAmount).isEqualTo(600.0); // 2 nights * 300.0

        Reservation updatedReservation = sut.getAllReservations().stream()
                .filter(r -> r.getId().equals(reservation.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(updatedReservation.getReservationStatus()).isEqualTo(ReservationStatus.FINALIZED);
    }

    @Test
    @DisplayName("Should throw exception when checkout with null date")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldThrowExceptionWhenCheckoutWithNullDate() {
        Room room = new Room("107", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Test Guest", 30, "11122233344");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 12, 16, 14, 0),
                LocalDateTime.of(2025, 12, 17, 11, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        assertThatThrownBy(() -> sut.checkout(reservation.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Checkout date cannot be null");
    }

    @Test
    @DisplayName("Should be possible to cancel a active reservation")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldBePossibleToCancelAActiveReservation(){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 10, 10, 0, 0));
        Reservation reservation = sut.createReservation(room, guest, stayPeriod);

        Reservation canceledReservation = sut.cancelReservation(reservation.getId());

        assertThat(canceledReservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("Should not be possible to cancel a non existent reservation")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToCancelANonExistentReservation(){
        assertThatThrownBy(() -> sut.cancelReservation("H-20251005223045384920"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Reservation not found for id: H-20251005223045384920");
    }

    @DisplayName("Should not be possible to cancel a non existent reservation")
    @ParameterizedTest(name = "Status = {0}")
    @CsvSource({"FINALIZED", "CANCELED"})
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldNotBePossibleToCancelANonActiveReservation(ReservationStatus invalidStatus){
        Room room = new Room("101", RoomStatus.AVAILABLE, 250.0);
        Guest guest = new Guest("Lucas", 38, "78609833038");
        StayPeriod stayPeriod = new StayPeriod(LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 10, 10, 0, 0));

        Reservation nonActiveReservation = new Reservation("H-20251005223045384920",room,guest,stayPeriod,invalidStatus);

        fakeRepository.save(nonActiveReservation);

        assertThatThrownBy(() -> sut.cancelReservation("H-20251005223045384920"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Reservation must be Active");
    }
}
