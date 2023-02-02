package be.vdab.geld.domain;

import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class MensTest {
    private Mens jan, mie;
    @BeforeEach
    void beforeEach() {
        jan = new Mens(1, "Jan", BigDecimal.TEN);
        mie = new Mens(1, "Mie", BigDecimal.ONE);
    }
    @Test
    void schenk() {
        jan.schenk(mie, BigDecimal.ONE);
        assertThat(jan.getGeld()).isEqualByComparingTo("9");
        assertThat(mie.getGeld()).isEqualByComparingTo("2");
    }
    @Test
    void schenkenMisluktBijOnvoldoendeGeld() {
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> jan.schenk(mie, BigDecimal.valueOf(11)));
    }
    @Test
    void bijSchenkenMoetAanMensIngevuldZijn() {
        assertThatNullPointerException().isThrownBy(
                () -> jan.schenk(null, BigDecimal.ONE));
    }
    @Test
    void bijSchenkenMoetBedragIngevuldZijn() {
        assertThatNullPointerException().isThrownBy(
                () -> jan.schenk(mie, null));
    }
}
