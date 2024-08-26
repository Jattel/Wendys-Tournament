package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;


@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + " (name, startDate, endDate) VALUES (?, ?, ?)";

  private static final String SQL_SELECT_SEARCH = "SELECT "
      + "* FROM " + TABLE_NAME
      + " WHERE (:name IS NULL OR UPPER(name) LIKE UPPER(CONCAT('%', :name, '%'))) "
      +  "AND (((:startDate IS NULL OR startDate >= :startDate) AND (:endDate IS NULL OR startDate <= :endDate)) OR "
      + "((:startDate IS NULL OR endDate >= :startDate) AND (:endDate IS NULL OR endDate <= :endDate)) OR "
      + "((:startDate IS NOT NULL AND startDate <= :startDate) AND (:endDate IS NOT NULL AND endDate >= :endDate)))";


  @Override
  public Tournament create(TournamentDetailDto tournament) {
    LOG.trace("create({})", tournament);


    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, tournament.name());
        ps.setString(2, tournament.startDate().toString());
        ps.setString(3, tournament.endDate().toString());

        return ps;
      }, keyHolder);
    } catch (DataAccessException e) {
      throw new FatalException(e.getMessage(), e);
    }

    Number id = keyHolder.getKey();
    if (id == null) {
      throw new FatalException("Could not extract key for newly created horse. "
          + "There is probably a programming error...");
    }

    return new Tournament()
        .setId((long) id)
        .setName(tournament.name())
        .setStart(tournament.startDate())
        .setEnd(tournament.endDate());
  }

  @Override
  public Tournament getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Tournament> tournaments;
    tournaments = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (tournaments.isEmpty()) {
      throw new NotFoundException("No tournament with ID %d found".formatted(id));
    }
    if (tournaments.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many tournament with ID %d found".formatted(id));
    }

    return tournaments.get(0);
  }

  @Override
  public Collection<Tournament> search(TournamentListDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;

    var params = new BeanPropertySqlParameterSource(searchParameters);

    return jdbcNamed.query(query, params, this::mapRow);
  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {
    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStart(result.getDate("startDate").toLocalDate())
        .setEnd(result.getDate("endDate").toLocalDate())
        ;
  }
}
