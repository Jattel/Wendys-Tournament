package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.ParticipantsMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.impl.ParticipantsJbdcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

@Service
public class ParticipantsServiceImpl implements ParticipantsService {
  private final ParticipantsValidator validator;
  private final ParticipantsJbdcDao dao;
  private final ParticipantsMapper mapper;

  public ParticipantsServiceImpl(ParticipantsValidator validator, ParticipantsJbdcDao dao, ParticipantsMapper mapper) {
    this.validator = validator;
    this.dao = dao;
    this.mapper = mapper;
  }
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public ParticipantsDto create(ArrayList<HorseParticipantDto> participants, Tournament tournament)
      throws ValidationException, NotFoundException, ConflictException {
    LOG.trace("create({}, {})", participants, tournament);
    validator.validateForCreate(participants);
    ArrayList<Participant> participant = dao.create(participants, tournament);
    return mapper.entityToDto(participant, tournament.getId());
  }

  @Override
  public ParticipantsDto getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);

    return mapper.entityToDto(dao.getByTournamentId(id), id);
  }

  @Override
  public ParticipantsDto update(ArrayList<HorseParticipantDto> participants, long id) throws ValidationException, NotFoundException, ConflictException {
    LOG.trace("update({}, {})", participants, id);
    validator.validateForUpdate(participants);
    ArrayList<Participant> updatedParticipants = dao.update(participants, id);
    return mapper.entityToDto(updatedParticipants, id);
  }
}
