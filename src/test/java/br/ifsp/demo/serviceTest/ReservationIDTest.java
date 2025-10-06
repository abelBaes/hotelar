package br.ifsp.demo.serviceTest;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.service.ReservationIDService;
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
import static org.mockito.Mockito.*;

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

    @DisplayName("Should validate an invalid Reservation ID.")
    @ParameterizedTest(name = "[{index}] - Invalid ID {0}")
    @Tag("UnitTest")
    @Tag("TDD")
    @MethodSource("invalidIDProvider")
    void shouldFalseToAnInvalidReservationID(String invalidID){
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
