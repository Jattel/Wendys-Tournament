package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /**
   * Convert a tournament entity object to a {@link TournamentDetailDto}.
   *
   * @param participants the participants needed for converting
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentDetailDto entityToDetailDto(Tournament tournament, ParticipantsDto participants) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStart(),
        tournament.getEnd(),
        participants.participantsArray()
    );
  }

  /**
   * Convert a tournament entity object to a {@link TournamentListDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStart(),
        tournament.getEnd()
    );
  }

  /**
   * Convert a tournament entity object and participantsDto to a {@link TournamentStandingsDto}.
   *
   * @param participants participants of the tournament
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentStandingsDto}.
   */
  public TournamentStandingsDto entityToStandingsDto(ParticipantsDto participants, Tournament tournament) {
    LOG.trace("entityToStandingsDto({}, {})", participants, tournament);
    LOG.debug("participants: ", participants.toString());
    TournamentStandingsTreeDto tree = standingsTreeCreation(participants.participantsArray(), 3, 1);
    LOG.debug("tree: ", tree);

    return new TournamentStandingsDto(
        tournament.getId(),
        tournament.getName(),
        participants.participantsArray(),
        tree
    );
  }

  private TournamentStandingsTreeDto standingsTreeCreation(ArrayList<HorseParticipantDto> participants, int level, int pos) {
    LOG.trace("standingsTreeCreation({}, {}, {})", participants, level, pos);
    HorseParticipantDto participant = null;
    TournamentStandingsTreeDto[] branch = null;

    if (level > 0) {
      branch = new TournamentStandingsTreeDto[2];
      branch[0] = standingsTreeCreation(participants, level - 1, pos * 2 - 1);
      if (branch[0].thisParticipant() != null) {
        if (branch[0].thisParticipant().roundReached() >= level) {
          participant = branch[0].thisParticipant();
          LOG.debug("newParticipant: " + participant);
        }
      }
      branch[1] = standingsTreeCreation(participants, level - 1, pos * 2);
      if (branch[1].thisParticipant() != null) {
        if (branch[1].thisParticipant().roundReached() >= level) {
          participant = branch[1].thisParticipant();
        }
      }
    } else {
      for (HorseParticipantDto p : participants) {
        if (p != null) {
          if (p.entryNumber() == pos && p.roundReached() >= 0) {
            participant = p;
          }
        }
      }
    }
    TournamentStandingsTreeDto tree = new TournamentStandingsTreeDto(participant, branch);
    LOG.debug(" tree in treeMethod: " + tree.branches() +  " thisParticipant: " + tree.thisParticipant() +  " lvl: " + level + " pos: " + pos);
    return tree;
  }

  /**
   * Updates tournament to {@link TournamentStandingsDto}.
   *
   * @param toUpdate the newest tournament
   * @return the updated participants in a list
   */
  public ArrayList<HorseParticipantDto> entityToUpdateStandingsDto(TournamentStandingsDto toUpdate) {
    LOG.trace("entityToUpdateStandingsDto({})", toUpdate);
    ArrayList<HorseParticipantDto> newParticipants = standingsTreeUpdate(new ArrayList<>(), toUpdate.tree(), 3, 1);

    return newParticipants;
  }

  private ArrayList<HorseParticipantDto> standingsTreeUpdate(ArrayList<HorseParticipantDto> newParticipants, TournamentStandingsTreeDto toUpdate,
                                                             int level, int pos) {
    LOG.trace("standingsTreeUpdate({}, {}, {}, {})", newParticipants, toUpdate, level, pos);

    if (level > 0) {
      TournamentStandingsTreeDto[] branch = toUpdate.branches();

      newParticipants = standingsTreeUpdate(newParticipants, branch[0], level - 1, pos * 2 - 1);
      LOG.debug("newParticipant: " + newParticipants);
      newParticipants = standingsTreeUpdate(newParticipants, branch[1], level - 1, pos * 2);

      int counter = -1;
      for (HorseParticipantDto p : newParticipants) {
        counter++;
        if (toUpdate.thisParticipant() != null) {
          LOG.debug("goes inside in here. p: " + p + " and toUpdate: " + toUpdate.thisParticipant().id() + " lvl: " + level);
          if (p.id() == toUpdate.thisParticipant().id() && p.roundReached() <= level) {
            newParticipants.set(counter, new HorseParticipantDto(p.id(), p.name(), p.dateOfBirth(), p.entryNumber(), level));
            break;
          }
        }
      }
    } else {
      if (toUpdate.thisParticipant() != null) {
        newParticipants.add(toUpdate.thisParticipant());
        newParticipants.set(newParticipants.size() - 1,
            new HorseParticipantDto(toUpdate.thisParticipant().id(), toUpdate.thisParticipant().name(), toUpdate.thisParticipant().dateOfBirth(), pos,
                level));
      }
    }


    LOG.debug("standingsUpdate: " + newParticipants);
    return newParticipants;
  }
}
