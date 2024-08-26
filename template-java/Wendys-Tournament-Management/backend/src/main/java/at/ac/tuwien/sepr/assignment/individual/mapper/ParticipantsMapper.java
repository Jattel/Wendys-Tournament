package at.ac.tuwien.sepr.assignment.individual.mapper;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

@Component
public class ParticipantsMapper {
  private final HorseService service;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public ParticipantsMapper(HorseService service) {
    this.service = service;
  }

  /**
   * Convert a participant entity object to a {@link ParticipantsDto}.
   *
   * @param participants the horse to convert
   * @return the converted {@link ParticipantsDto}
   */

  public ParticipantsDto entityToDto(ArrayList<Participant> participants, long id) throws NotFoundException {
    LOG.trace("entityToDto({}, {})", participants, id);
    ArrayList<HorseParticipantDto> participantsArray = new ArrayList<>();
    for (int i = 0; i < participants.size(); i++) {
      HorseParticipantDto participantDto = service.getParticipantById(participants.get(i).getHorseId(), participants);
      LOG.debug("getParticipant: " + participantDto);
      if (participantDto != null) {
        participantsArray.add(participantDto);
      }
    }

    return new ParticipantsDto(id, participantsArray);
  }
}
