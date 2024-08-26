package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.ArrayList;

public interface ParticipantsService {
  /**
   * Create a new participants in the persistent data store.
   *
   * @param participants the data for the new participants
   * @param tournament the tournament the participant takes in
   * @return the participants, that was just newly created in the persistent data store
   */
  ParticipantsDto create(ArrayList<HorseParticipantDto> participants, Tournament tournament) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Get participants by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to get
   * @return the participants
   * @throws NotFoundException if the participants with the given ID does not exist in the persistent data store
   */
  ParticipantsDto getById(long id) throws NotFoundException;

  /**
   * Updates participants in the tournament with the given id
   *
   * @param participants the participants that are
   * @param id tournament id
   * @return the participants
   * @throws NotFoundException if the participants with the given ID does not exist in the persistent data store
   * @throws ValidationException if the participants input is not valid
   */
  ParticipantsDto update(ArrayList<HorseParticipantDto> participants, long id) throws NotFoundException, ValidationException, ConflictException;
}
