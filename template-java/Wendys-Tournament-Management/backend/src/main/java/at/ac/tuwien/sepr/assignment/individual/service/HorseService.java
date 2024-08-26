package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Search for horses in the persistent data store matching all provided fields.
   * The name is considered a match, if the search string is a substring of the field in horse.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the horses where the given fields match.
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters);

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (no name, name too long …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (breed does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;


  /**
   * Get the horse with given ID, with more detail information.
   * This includes the breed of the horse.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Create a new horse in the persistent data store.
   *
   * @param horse the data for the new horse
   * @return the horse, that was just newly created in the persistent data store
   * @throws ValidationException if the data given for the horse creation is invalid (no name, …)
   */
  HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException;
  /**
   * Delete a horse in the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   */
  void deleteById(Long id) throws NotFoundException, ConflictException;

  /**
   * Gets horse of the matching participant
   *
   * @param id horse id
   * @param participant participants list to check if they match with the horse
   * @return the horse that matched the participant
   * @throws NotFoundException if horse id can not be found in horse database
   */
  HorseParticipantDto getParticipantById(long id, List<Participant> participant) throws NotFoundException;
}
