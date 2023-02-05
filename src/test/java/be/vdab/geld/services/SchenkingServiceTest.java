package be.vdab.geld.services;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import be.vdab.geld.repositories.MensRepository;
import be.vdab.geld.repositories.SchenkingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchenkingServiceTest {
    private SchenkingService schenkingService;
    @Mock
    private SchenkingRepository schenkingRepository;
    @Mock
    private MensRepository mensRepository;
    private Mens jan, mie;
    @BeforeEach
    void beforeEach() {
        schenkingService = new SchenkingService(schenkingRepository, mensRepository);
        jan = new Mens(1, "Jan", BigDecimal.TEN);
        mie = new Mens(2, "Mie", BigDecimal.TEN);
    }
    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> schenkingService.create(new Schenking(1, 2, BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnbestaandeAanMensMislukt() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> schenkingService.create(new Schenking(1, 2, BigDecimal.ONE)));
    }
    @Test
    void schenk() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        when(mensRepository.findAndLockById(2)).thenReturn(Optional.of(mie));
        var schenking = new Schenking(1, 2, BigDecimal.ONE);
        schenkingService.create(schenking);
        assertThat(jan.getGeld()).isEqualByComparingTo("9");
        assertThat(mie.getGeld()).isEqualByComparingTo("11");
        verify(mensRepository).findAndLockById(1);
        verify(mensRepository).findAndLockById(2);
        verify(mensRepository).update(jan);
        verify(mensRepository).update(mie);
        verify(schenkingRepository).create(schenking);
    }
    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        when(mensRepository.findAndLockById(2)).thenReturn(Optional.of(mie));
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> schenkingService.create(new Schenking(1, 2, BigDecimal.valueOf(11))));
    }
}
