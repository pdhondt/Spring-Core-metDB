package be.vdab.geld.repositories;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.exceptions.MensNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class MensRepository {
    private final JdbcTemplate template;
    private final RowMapper<Mens> mensMapper = (result, rowNum) ->
            new Mens(result.getLong("id"), result.getString("naam"),
                    result.getBigDecimal("geld"));
    public MensRepository(JdbcTemplate template) {
        this.template = template;
    }
    public long findAantal() {
        var sql = """
                select count(*)
                from mensen
                """;
        return template.queryForObject(sql, Long.class);
    }
    public void delete(long id) {
        var sql = """
                delete from mensen
                where id = ?
                """;
        template.update(sql, id);
    }
    public void update(Mens mens) {
        var sql = """
                update mensen
                set naam = ?, geld = ?                
                where id = ?
                """;
        if (template.update(sql,
                mens.getNaam(), mens.getGeld(), mens.getId()) == 0) {
            throw new MensNietGevondenException(mens.getId());
        }
    }
    /*public void create(Mens mens) {
        var sql = """
                insert into mensen(naam, geld)
                values (?, ?)
                """;
        template.update(sql, mens.getNaam(), mens.getGeld());
    }*/
    public long create(Mens mens) {
        var sql = """
                insert into mensen(naam, geld)
                values (?, ?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, mens.getNaam());
            statement.setBigDecimal(2, mens.getGeld());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
    public List<Mens> findAll() {
        var sql = """
                select id, naam, geld
                from mensen
                order by id
                """;
        return template.query(sql, mensMapper);
    }
    public List<Mens> findByGeldBetween(BigDecimal van, BigDecimal tot) {
        var sql = """
                select id, naam, geld
                from mensen
                where geld between ? and ?
                order by geld
                """;
        return template.query(sql, mensMapper, van, tot);
    }
    public Optional<Mens> findById(long id) {
        try {
            var sql = """
                    select id, naam, geld
                    from mensen
                    where id = ?
                    """;
            return Optional.of(template.queryForObject(sql, mensMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }
    public Optional<Mens> findAndLockById(long id) {
        try {
            var sql = """
                    select id, naam, geld
                    from mensen
                    where id = ?
                    for update
                    """;
            return Optional.of(template.queryForObject(sql, mensMapper, id));
            } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }
}
