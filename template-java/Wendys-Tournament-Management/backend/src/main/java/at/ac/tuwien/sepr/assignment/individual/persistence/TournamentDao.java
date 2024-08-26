package at.ac.tuwien.sepr.assignment.individual.persistence;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

public interface TournamentDao {
  /**
   * Create a new tournament in the persistent data store.
   *
   * @param tournament the data for the new tournament
   * @return the tournament, that was just newly created in the persistent data store
   */
  Tournament create(TournamentDetailDto tournament);
  /**
   * Get tournament by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to get
   * @return the tournament
   * @throws NotFoundException if the tournament with the given ID does not exist in the persistent data store
   */
  Tournament getById(long id) throws NotFoundException;

  /**
   *  Get the tournaments that match the given search parameters.
   *  Parameters that are {@code null} are ignored.
   *  The name is considered a match, if the given parameter is a substring of the field in tournament.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the tournaments where all given parameters match.
   */
  Collection<Tournament> search(TournamentListDto searchParameters);
}
