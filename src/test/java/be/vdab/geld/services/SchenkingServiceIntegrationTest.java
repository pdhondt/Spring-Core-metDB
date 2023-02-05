package be.vdab.geld.services;

import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import be.vdab.geld.repositories.MensRepository;
import be.vdab.geld.repositories.SchenkingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@JdbcTest
@Import({SchenkingService.class, SchenkingRepository.class, MensRepository.class})
@Sql("/mensen.sql")
public class SchenkingServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String MENSEN = "mensen";
    private static final String SCHENKINGEN = "schenkingen";
    private final SchenkingService schenkingService;

    public SchenkingServiceIntegrationTest(SchenkingService schenkingService) {
        this.schenkingService = schenkingService;
    }
    private long idVanTestMens1() {
        return jdbcTemplate.queryForObject(
                "select id from mensen where naam = 'test1'", Long.class);
    }
    private long idVanTestMens2() {
        return jdbcTemplate.queryForObject(
                "select id from mensen where naam = 'test2'", Long.class);
    }
    @Test
    void create() {
        var vanMensId = idVanTestMens1();
        var aanMensId = idVanTestMens2();
        schenkingService.create(
                new Schenking(vanMensId, aanMensId, BigDecimal.ONE));
                assertThat(countRowsInTableWhere(MENSEN,
                        "geld = 999 and id = " + vanMensId)).isOne();
                assertThat(countRowsInTableWhere(MENSEN,
                        "geld = 2001 and id = " + aanMensId)).isOne();
                assertThat(countRowsInTableWhere(SCHENKINGEN,
                        "bedrag = 1 and vanMensId = " + vanMensId +
                        " and aanMensId = " + aanMensId)).isOne();
    }
    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> schenkingService.create(
                        new Schenking(Long.MAX_VALUE, idVanTestMens2(), BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnbestaandeAanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> schenkingService.create(
                        new Schenking(idVanTestMens1(), Long.MAX_VALUE, BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        var vanMensId = idVanTestMens1();
        var aanMensId = idVanTestMens2();
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> schenkingService.create(
                        new Schenking(vanMensId, aanMensId, BigDecimal.valueOf(1_001))));
    }
}
