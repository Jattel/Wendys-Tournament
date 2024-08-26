package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.ParticipantsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ParticipantsJbdcDao implements ParticipantsDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "participants";

  private static final String SQL_CREATE =
      "INSERT INTO " + TABLE_NAME + " (horse_id, tournament_id, entryNumber, roundReached) VALUES ( ?, ?, ?, ?)";

  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE tournament_id = ?";
  private static final String SQL_SELECT_BY_ID_HORSE = "SELECT * FROM " + TABLE_NAME + " WHERE horse_id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET entryNumber = ?"
      + "  , roundReached = ?"
      + " WHERE horse_id = ? AND tournament_id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public ParticipantsJbdcDao(NamedParameterJdbcTemplate jdbcNamed,
                             JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }


  @Override
  public ArrayList<Participant> create(ArrayList<HorseParticipantDto> newParticipants, Tournament tournament) throws FatalException {
    LOG.trace("create({})", newParticipants);
    ArrayList<Participant> participantList = new ArrayList<>();

    KeyHolder keyHolder = new GeneratedKeyHolder();
    for (int i = 0; i < 8; i++) {
      final int index = i;
      try {
        jdbcTemplate.update(con -> {
          PreparedStatement ps = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
          ps.setFloat(1, newParticipants.get(index).id());
          ps.setFloat(2, tournament.getId());
          ps.setInt(3, 0);
          ps.setInt(4, 0);

          return ps;
        }, keyHolder);
      } catch (DataAccessException e) {
        throw new FatalException(e.getMessage(), e);
      }
      participantList.add(new Participant()
          .setHorseId(newParticipants.get(i).id())
          .setTournamentId(tournament.getId())
          .setEntryNumber(0)
          .setRoundReached(0)
      );
    }


    return participantList;
  }

  @Override
  public ArrayList<Participant> getByTournamentId(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Participant> participants;
    participants = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);
    if (participants.isEmpty()) {
      LOG.debug("Gets here");
      throw new NotFoundException("No participant with ID %d found".formatted(id));
    }
    for (Participant p : participants) {
      LOG.debug("participants: " + p.getHorseId() + " " + p.getTournamentId());
    }
    ArrayList<Participant> p = (ArrayList<Participant>) participants;
    LOG.debug("participants: " + p);
    return p;
  }

  @Override
  public boolean participantExists(long id) {
    LOG.trace("participantExists({})", id);
    List<Participant> participants;
    participants = jdbcTemplate.query(SQL_SELECT_BY_ID_HORSE, this::mapRow, id);
    if (participants.isEmpty()) {
      return false;
    }
    for (Participant p : participants) {
      LOG.debug("participants: " + p.getHorseId() + " " + p.getTournamentId());
    }
    ArrayList<Participant> p = (ArrayList<Participant>) participants;
    LOG.debug("participants: " + p);
    return true;
  }

  @Override
  public ArrayList<Participant> update(ArrayList<HorseParticipantDto> participants, long id) throws NotFoundException {
    LOG.trace("update({}, {})", participants, id);
    ArrayList<Participant> newParticipants = new ArrayList<>();
    for (HorseParticipantDto p : participants) {
      LOG.debug("EN: " + p.entryNumber());
      LOG.debug("RR: " + p.roundReached());
      int updated = jdbcTemplate.update(SQL_UPDATE,
          p.entryNumber(),
          p.roundReached(),
          p.id(),
          id);
      if (updated == 0) {
        throw new NotFoundException("Could not update participant with ID " + p.id() + ", because it does not exist");
      }
      newParticipants.add(new Participant()
          .setHorseId(p.id())
          .setTournamentId(id)
          .setEntryNumber(p.entryNumber())
          .setRoundReached(p.roundReached()));
    }
    return newParticipants;
  }


  private Participant mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Participant()
        .setTournamentId(resultSet.getLong("tournament_id"))
        .setHorseId(resultSet.getLong("horse_id"))
        .setEntryNumber(resultSet.getInt("entryNumber"))
        .setRoundReached(resultSet.getInt("roundReached"))
        ;
  }
}
