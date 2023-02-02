package be.vdab.geld.repositories;

import be.vdab.geld.domain.Schenking;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(SchenkingRepository.class)
@Sql("/mensen.sql")
public class SchenkingRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String SCHENKINGEN = "schenkingen";
    private final SchenkingRepository schenkingRepository;

    public SchenkingRepositoryTest(SchenkingRepository schenkingRepository) {
        this.schenkingRepository = schenkingRepository;
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
        schenkingRepository.create(
                new Schenking(vanMensId, aanMensId, BigDecimal.ONE));
        assertThat(countRowsInTableWhere(SCHENKINGEN, "vanMensId = " + vanMensId +
                " and aanMensId = " + aanMensId)).isOne();
    }
}
