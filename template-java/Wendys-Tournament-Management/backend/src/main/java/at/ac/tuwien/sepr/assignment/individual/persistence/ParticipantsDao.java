package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.ArrayList;

public interface ParticipantsDao {

  /**
   * Create a new participants in the persistent data store.
   *
   * @param newParticipants the data for the new participants
   * @return the participants, that was just newly created in the persistent data store
   * @throws FatalException  if the participants could not extract key for newly created participants
   */
  ArrayList<Participant> create(ArrayList<HorseParticipantDto> newParticipants, Tournament tournament) throws FatalException;

  /**
   * Get participants by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to get
   * @return the participants
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  ArrayList<Participant> getByTournamentId(long id) throws NotFoundException;

  /**
   * Checks if participant exists in the persistent layer
   *
   * @param id horse id
   * @return true if participant exists
   */
  boolean participantExists(long id);

  /**
   * Updates participants in the tournament with the id
   *
   * @param participants the updated participants
   * @param id tournament id
   * @return the updated participants
   * @throws NotFoundException if participants cannot be found
   */
  ArrayList<Participant> update(ArrayList<HorseParticipantDto> participants, long id) throws NotFoundException;
}
