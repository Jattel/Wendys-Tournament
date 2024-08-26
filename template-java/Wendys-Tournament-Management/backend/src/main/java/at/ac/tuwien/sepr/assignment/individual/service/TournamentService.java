package at.ac.tuwien.sepr.assignment.individual.service;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

public interface TournamentService {

  /**
   * Create a new tournament in the persistent data store.
   *
   * @param tournament the data for the new tournament
   * @return the tournament, that was just newly created in the persistent data store
   * @throws ValidationException if the data given for the tournament creation is invalid
   */
  TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, NotFoundException, ConflictException;

  /**
   * Get the tournament with given ID, with more detail information.
   *
   * @param id the ID of the tournament to get
   * @return the tournament with ID {@code id}
   * @throws NotFoundException if the tournament with the given ID does not exist in the persistent data store
   */
  TournamentStandingsDto getById(long id) throws NotFoundException;

  /**
   *  Search for tournaments in the persistent data store matching all provided fields.
   *  The name is considered a match, if the search string is a substring of the field in tournaments.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the horses where the given fields match.
   */
  Stream<TournamentListDto> search(TournamentListDto searchParameters);

  /**
   * update the current tournament standings with the updated tournament and the tournament id
   *
   * @param id tournament id
   * @param toUpdate the updated tournament
   * @return the updated {@link TournamentStandingsDto}
   * @throws NotFoundException if tournament with the id is not found
   * @throws ValidationException if given updated tournament has invalid values
   */
  TournamentStandingsDto update(long id, TournamentStandingsDto toUpdate) throws NotFoundException, ValidationException, ConflictException;
}
