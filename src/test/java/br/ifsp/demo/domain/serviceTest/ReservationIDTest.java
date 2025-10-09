package br.ifsp.demo.domain.serviceTest;

import br.ifsp.demo.domain.service.ReservationIDService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationIDTest {

    static Stream<Arguments> invalidIDProvider(){
        return Stream.of(
                Arguments.of("COISA123"),
                Arguments.of("H-202510052230453849201"),
                Arguments.of("H-2025100522304538492"),
                Arguments.of("1220251005223045384920")
        );
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("Should validate an valid Reservation ID.")
    void shouldTrueToAnValidReservationID(){
        var ValidReservationId = "H-20251005223045384920";
        assertThat(ReservationIDService.validate(ValidReservationId)).isTrue();
    }

    @DisplayName("Should throw IllegalArgumentException when validate an invalid Reservation ID.")
    @ParameterizedTest(name = "[{index}] - Invalid ID {0}")
    @Tag("UnitTest")
    @Tag("TDD")
    @MethodSource("invalidIDProvider")
    void shouldThrowIllegalArgumentExceptionToAnInvalidReservationID(String invalidID){
        assertThatThrownBy(() -> ReservationIDService.validate(invalidID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Reservation ID format");
    }

    @Test
    @DisplayName("Should throw an NullPointerException when Reservation ID is null.")
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowAnNullPointerExceptionWhenReservationIdIsNull(){
        assertThatThrownBy(() -> ReservationIDService.validate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Reservation ID must not be null");
    }

}
