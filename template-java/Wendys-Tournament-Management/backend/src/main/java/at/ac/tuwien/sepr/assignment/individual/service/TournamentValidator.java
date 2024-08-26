package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
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
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;

  public TournamentValidator(HorseDao horseDao) {
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
   * Validates tournament for creation
   *
   * @param tournament input that is getting validated
   * @throws ValidationException if input's value is invalid
   */
  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.id() != null) {
      validationErrors.add("Tournament id is invalid.");
    }

    if (tournament.name() != null) {
      if (tournament.name().isBlank()) {
        validationErrors.add("Tournament name is given but blank");
      }
      if (!isValidName(tournament.name())) {
        validationErrors.add("Tournament name is not valid.");
      }
      if (tournament.name().length() > 255) {
        validationErrors.add("Tournament name too long: longer than 255 characters");
      }
    }

    if (tournament.startDate() == null) {
      validationErrors.add("Start date is not given.");
    }

    if (tournament.endDate() == null) {
      validationErrors.add("End date is not given.");
    }

    if (tournament.startDate().isAfter(tournament.endDate())) {
      validationErrors.add("Tournament's start date is after the its end date.");
    }
    if (tournament.participants() == null) {
      validationErrors.add("Participants do not exist.");
    }
    if (tournament.participants().size() != 8) {
      validationErrors.add("Participants are not 8.");
    }

    for (HorseParticipantDto horse : tournament.participants()) {
      if (horse.dateOfBirth().isAfter(tournament.startDate())) {
        validationErrors.add("Horse " + horse.name() + " was born after tournament starts.");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for creation failed", validationErrors);
    }
  }

  /**
   * Validates tournament for update
   *
   * @param tournament input that is getting validated
   * @throws ValidationException if tournament's value is invalid
   */
  public void validateToUpdate(TournamentStandingsDto tournament) throws ValidationException {
    LOG.trace("validateToUpdate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.id() == null) {
      validationErrors.add("Id is not valid.");
    }

    if (tournament.name() != null) {
      if (tournament.name().isBlank()) {
        validationErrors.add("Tournament name is given but blank");
      }
      if (!isValidName(tournament.name())) {
        validationErrors.add("Tournament name is not valid.");
      }
      if (tournament.name().length() > 255) {
        validationErrors.add("Tournament name too long: longer than 255 characters");
      }
    }

    if (tournament.participants().size() != 8) {
      validationErrors.add("Participants are not 8.");
    }
    try {
      ArrayList<HorseParticipantDto> participantsList = checker(new ArrayList<>(), tournament.tree(), 3, 1);
      Set<Long> uniqueIds = new HashSet<>();
      for (HorseParticipantDto horse : participantsList) {
        LOG.debug("Id: " + horse.id() + " Size: " + participantsList.size());
        if (!uniqueIds.add(horse.id())) {
          LOG.debug("Comes here: " + horse.id() + "Size: " + participantsList.size());
          validationErrors.add("Duplicate participants given.");
        }
      }
    } catch (ValidationException e) {
      validationErrors.add("Tree input is not valid");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for creation failed", validationErrors);
    }
  }


  private ArrayList<HorseParticipantDto> checker(ArrayList<HorseParticipantDto> newParticipants, TournamentStandingsTreeDto tournament, int level, int pos)
      throws ValidationException {
    LOG.trace("checker({}, {}, {}, {})", newParticipants, tournament, level, pos);
    List<String> validationErrors = new ArrayList<>();


    if (level > 0) {
      TournamentStandingsTreeDto[] branch = tournament.branches();
      if (branch != null) {
        if (branch.length != 2) {
          validationErrors.add("Tree structure is not valid.");
        }
      }
      newParticipants = checker(newParticipants, branch[0], level - 1, pos * 2 - 1);
      LOG.debug("newParticipant: " + newParticipants);
      newParticipants = checker(newParticipants, branch[1], level - 1, pos * 2);

      int counter = -1;
      for (HorseParticipantDto p : newParticipants) {
        counter++;
        if (tournament.thisParticipant() != null) {
          LOG.debug("goes inside in here. p: " + p + " and tournament: " + tournament.thisParticipant().id());
          if (p.id() == tournament.thisParticipant().id() && tournament.thisParticipant().roundReached() <= level) {
            newParticipants.set(counter, new HorseParticipantDto(p.id(), p.name(), p.dateOfBirth(), p.entryNumber(), level));
            break;
          }
        }
      }

      if (tournament.thisParticipant() != null) {
        if (tournament.branches()[0].thisParticipant() != null && tournament.branches()[1].thisParticipant() != null) {
          if (!(tournament.thisParticipant().id() == tournament.branches()[0].thisParticipant().id()
              || tournament.thisParticipant().id() == tournament.branches()[1].thisParticipant().id())) {
            validationErrors.add("Winner is not from previous winners.");
          }
        } else {
          validationErrors.add("Cannot set a winner since previous winners are not set.");
        }
      }
    } else {
      if (tournament.thisParticipant() != null) {
        newParticipants.add(tournament.thisParticipant());
        newParticipants.set(newParticipants.size() - 1,
            new HorseParticipantDto(tournament.thisParticipant().id(), tournament.thisParticipant().name(), tournament.thisParticipant().dateOfBirth(), pos,
                level));
      }
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of standing for creation failed", validationErrors);
    }
    return newParticipants;
  }
}
