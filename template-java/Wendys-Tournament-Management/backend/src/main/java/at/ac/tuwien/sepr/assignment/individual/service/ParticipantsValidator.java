package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParticipantsValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;

  public ParticipantsValidator(HorseDao horseDao) {
    this.horseDao = horseDao;
  }

  private static boolean isValidName(String name) {
    LOG.trace("isValidName({})", name);
    String pattern = "^[A-Za-z]+(?:\\s[A-Za-z]+)*$";
    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(name);
    return matcher.matches();
  }

  /**
   * Validates participants for creation
   *
   * @param participants input that is being validated
   * @throws ValidationException if input has invalid values
   */
  public void validateForCreate(ArrayList<HorseParticipantDto> participants) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", participants);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (participants.size() != 8) {
      validationErrors.add("Participants are not 8.");
    }
    Horse serviceDto = null;
    for (HorseParticipantDto horseParticipantDto : participants) {
      LOG.debug("Participant id: " + horseParticipantDto.id());
      try {
        serviceDto = horseDao.getById(horseParticipantDto.id());
      } catch (NotFoundException e) {
        validationErrors.add("Participant does not exist in the horse data base.");
      }
      Set<Long> uniqueIds = new HashSet<>();
      if (!uniqueIds.add(serviceDto.getId())) {
        validationErrors.add("Duplicate participants given.");
      }


      if (serviceDto == null) {
        validationErrors.add("Participant does not exist in the horse data base.");
      }

      if (serviceDto.getId() == horseParticipantDto.id()) {
        if (!serviceDto.getName().equals(horseParticipantDto.name())) {
          conflictErrors.add("Participant's name does not match the one in the database.\n" + serviceDto.getName() + "    " + horseParticipantDto.name());
        }
        if (!serviceDto.getDateOfBirth().equals(horseParticipantDto.dateOfBirth())) {
          conflictErrors.add("Participant's date of birth does not match the one in the database.");
        }
      } else {
        conflictErrors.add("Participant's ID does not match the one in the database.");
      }
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for creation failed", validationErrors);
    }
  }

  /**
   * Validates participants for update
   *
   * @param participants input that is being validated
   * @throws ValidationException if participants value is invalid
   * @throws NotFoundException if participant cannot be found in persistent layer
   */
  public void validateForUpdate(ArrayList<HorseParticipantDto> participants) throws ValidationException, NotFoundException, ConflictException {
    LOG.trace("validateForCreate({})", participants);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (participants.size() != 8) {
      validationErrors.add("Participants are not 8.");
    }
    Horse horse = null;
    for (HorseParticipantDto horseParticipantDto : participants) {
      try {
        horse = horseDao.getById(horseParticipantDto.id());
      } catch (NotFoundException e) {
        conflictErrors.add("Participant does not exist in the horse data base.");
      }
      Set<Long> uniqueIds = new HashSet<>();
      if (!uniqueIds.add(horse.getId())) {
        validationErrors.add("Duplicate participants given.");
      }
      if (horseParticipantDto.name() == null) {
        validationErrors.add("Participant's name is invalid.");
      }
      if (horseParticipantDto.name() != null) {
        if (horseParticipantDto.name().isBlank()) {
          validationErrors.add("Participant's name is given but blank");
        }
        if (!isValidName(horseParticipantDto.name())) {
          validationErrors.add("Participant's name is not valid.");
        }
        if (horseParticipantDto.name().length() > 255) {
          validationErrors.add("Participant's name too long: longer than 255 characters");
        }
      }

      if (horseParticipantDto.entryNumber() > 8 || horseParticipantDto.entryNumber() < 0) {
        validationErrors.add("Participant's entry number is not valid.");
      }
      if (horseParticipantDto.roundReached() > 3 || horseParticipantDto.roundReached() < 0) {
        validationErrors.add("Participant's round reached is invalid");
      }
      if (horseDao.getById(horseParticipantDto.id()) == null) {
        validationErrors.add("Participant is not valid. Cannot be found in the persistent layer.");
      }
    }
    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for creation failed", validationErrors);
    }
  }
}
