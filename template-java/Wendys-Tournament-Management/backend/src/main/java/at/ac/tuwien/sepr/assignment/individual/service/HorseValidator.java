package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.tuwien.sepr.assignment.individual.persistence.BreedDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.ParticipantsDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;
  private final BreedDao breedDao;
  private final ParticipantsDao participantsDao;

  public HorseValidator(HorseDao horseDao, BreedDao breedDao, ParticipantsDao participantsDao) {
    this.horseDao = horseDao;
    this.breedDao = breedDao;
    this.participantsDao = participantsDao;
  }


  private static boolean isValidName(String name) {
    LOG.trace("isValidName({})", name);
    String pattern = "^[A-Za-z]+(?:\\s[A-Za-z]+)*$";
    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(name);
    return matcher.matches();
  }

  /**
   * Validates Horse before deletion
   *
   * @param id horse id
   * @throws ConflictException if horse is in tournament
   */
  public void validateForDelete(Long id) throws ConflictException {
    LOG.trace("validateForDelete({})", id);
    List<String> conflictErrors = new ArrayList<>();


    if (this.participantsDao.participantExists(id)) {
      conflictErrors.add("Horse exits in tournament. Cannot be deleted.");
    }



    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for delete failed", conflictErrors);
    }
  }

  /**
   * Validates Horse before update
   *
   * @param horse horse getting updated
   * @throws ValidationException if horse's values are invalid
   * @throws ConflictException if horse's breed does not exist in the persistent layer unless it is null
   */
  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }
    if (horse.name() == null) {
      validationErrors.add("Horse name should be defined");
    } else {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
      if (!isValidName(horse.name())) {
        validationErrors.add("Horse name is not valid.");
      }
    }

    if (horse.sex() != null) {
      if (!(horse.sex() == Sex.FEMALE || horse.sex() == Sex.MALE)) {
        validationErrors.add("Horse need to have a defined sex.");
      }
    } else {
      validationErrors.add("Sex is not declared.");
    }

    if (horse.dateOfBirth() != null) {
      if (horse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Horse birthday lays in the future.");
      }
    } else {
      validationErrors.add("Date of birth is not declared.");
    }



    if (horse.height() >= 100.00) {
      validationErrors.add("Height can not be bigger than or as big as 100.00.");
    }
    if (horse.height() <= 0.00) {
      validationErrors.add("Height can not be a negative number.");
    }

    if (horse.weight() >= 1000000.00) {
      validationErrors.add("Weight can not be bigger than or as big as 1000000.00.");
    }
    if (horse.weight() <= 0.00) {
      validationErrors.add("Weight can not be a negative number.");
    }

    Set<Long> breedId = new HashSet<>();
    if (horse.breed() != null) {
      breedId.add(horse.breed().id());
      if (breedDao.findBreedsById(breedId).size() != 1) {
        conflictErrors.add("Breed does not exist.");
      }
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  /**
   * Validates horse before creation
   *
   * @param horse input to be validated
   * @throws ValidationException if horse's values are invalid
   * @throws ConflictException if horse's breed does not exist in the persistent layer unless it is null
   */
  public void validateForCreate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horse.name() == null) {
      validationErrors.add("Horse name should be defined");
    } else {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
      if (!isValidName(horse.name())) {
        validationErrors.add("Horse name is not valid.");
      }
    }

    if (horse.sex() != null) {
      if (!(horse.sex() == Sex.FEMALE || horse.sex() == Sex.MALE)) {
        validationErrors.add("Horse need to have a defined sex.");
      }
    } else {
      validationErrors.add("Sex is not declared.");
    }

    if (horse.dateOfBirth() != null) {
      if (horse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Horse birthday lays in the future.");
      }
    } else {
      validationErrors.add("Date of birth is not declared.");
    }


    if (horse.height() >= 100.00) {
      validationErrors.add("Height can not be bigger than or as big as 100.00.");
    }
    if (horse.height() <= 0.00) {
      validationErrors.add("Height can not be a negative number.");
    }

    if (horse.weight() >= 1000000.00) {
      validationErrors.add("Weight can not be bigger than or as big as 1000000.00.");
    }
    if (horse.weight() <= 0.00) {
      validationErrors.add("Weight can not be a negative number.");
    }

    Set<Long> breedId = new HashSet<>();
    if (horse.breed() != null) {
      breedId.add(horse.breed().id());
      if (breedDao.findBreedsById(breedId).size() != 1) {
        conflictErrors.add("Breed does not exist.");
      }
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }
}


