package be.vdab.geld.repositories;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.exceptions.MensNietGevondenException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@JdbcTest
@Import(MensRepository.class)
@Sql("/mensen.sql")
public class MensRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String MENSEN = "mensen";
    private final MensRepository mensRepository;
    public MensRepositoryTest(MensRepository mensRepository) {
        this.mensRepository = mensRepository;
    }
    @Test
    void findAantal() {
        assertThat(mensRepository.findAantal())
                .isEqualTo(countRowsInTable(MENSEN));
    }
    @Test
    void findAllGeeftAlleMensenGesorteerdOpId() {
        assertThat(mensRepository.findAll())
                .hasSize(countRowsInTable(MENSEN))
                .extracting(Mens::getId)
                .isSorted();
    }
    @Test
    void create() {
        var id = mensRepository.create(new Mens(0, "test3", BigDecimal.TEN));
        assertThat(id).isPositive();
        assertThat(countRowsInTableWhere(MENSEN, "id = " + id)).isOne();
    }
    private long idVanTestMens1() {
        return jdbcTemplate.queryForObject(
                "select id from mensen where naam = 'test1'", Long.class);
    }
    @Test
    void delete() {
        var id = idVanTestMens1();
        mensRepository.delete(id);
        assertThat(countRowsInTableWhere(MENSEN, "id = " + id)).isZero();
    }
    @Test
    void findById() {
        assertThat(mensRepository.findById(idVanTestMens1())).hasValueSatisfying(
                mens -> assertThat(mens.getNaam()).isEqualTo("test1"));
    }
    @Test
    void findByOnbestaandeIdVindtGeenMens() {
        assertThat(mensRepository.findById(Long.MAX_VALUE)).isEmpty();
    }
    @Test
    void update() {
        var id = idVanTestMens1();
        var mens = new Mens(id, "mens1", BigDecimal.TEN);
        mensRepository.update(mens);
        assertThat(countRowsInTableWhere(MENSEN, "geld = 10 and id = " + id)).isOne();
    }
    @Test
    void updateOnbestaandeMensGeeftEenFout() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> mensRepository.update(
                        new Mens(Long.MAX_VALUE, "test3", BigDecimal.TEN)));
    }
    @Test
    void findAndLockById() {
        assertThat(mensRepository.findAndLockById(idVanTestMens1()))
                .hasValueSatisfying(
                        mens -> assertThat(mens.getNaam()).isEqualTo("test1"));
    }
    @Test
    void findAndLockByOnbestaandeIdVindtGeenMens() {
        assertThat(mensRepository.findAndLockById(Long.MAX_VALUE)).isEmpty();
    }
    @Test
    void findByGeldBetween() {
        var van = BigDecimal.ONE;
        var tot = BigDecimal.TEN;
        assertThat(mensRepository.findByGeldBetween(van, tot))
                .hasSize(super.countRowsInTableWhere(MENSEN, "geld between 1 and 10"))
                .extracting(Mens::getGeld)
                .allSatisfy(geld -> assertThat(geld).isBetween(van, tot))
                .isSorted();
    }
    @Test
    void findSchenkStatistiekPerMens() {
        var statistiek = mensRepository.findSchenkStatistiekPerMens();
        assertThat(statistiek).hasSize(super.jdbcTemplate.queryForObject(
                "select count(distinct vanMensId) from schenkingen", Integer.class))
                .extracting(statistiekRij -> statistiekRij.id()).isSorted();
        var rij1 = statistiek.get(0);
        assertThat(rij1.aantal()).isEqualTo(super.jdbcTemplate.queryForObject(
                "select count(*) from schenkingen where vanMensId = " + rij1.id(), Integer.class));
        assertThat(rij1.totaal()).isEqualTo(super.jdbcTemplate.queryForObject(
                "select sum(bedrag) from schenkingen where vanMensId = " + rij1.id(), BigDecimal.class));
    }
}
