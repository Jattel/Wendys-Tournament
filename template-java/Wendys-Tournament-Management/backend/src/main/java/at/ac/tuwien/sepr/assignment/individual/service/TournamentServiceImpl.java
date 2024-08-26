package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;


@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao dao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;
  private final ParticipantsService participantsService;
  private final ParticipantsValidator participantsValidator;

  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper, TournamentValidator validator, ParticipantsService participantsService,
                               ParticipantsValidator participantsValidator) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.participantsService = participantsService;
    this.participantsValidator = participantsValidator;
  }

  @Transactional
  @Override
  public TournamentDetailDto create(TournamentDetailDto tournamentDetailDto) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("create({})", tournamentDetailDto);
    validator.validateForCreate(tournamentDetailDto);
    Tournament tournament = dao.create(tournamentDetailDto);
    ParticipantsDto participants = participantsService.create(tournamentDetailDto.participants(), tournament);

    return mapper.entityToDetailDto(tournament, participants);
  }

  @Override
  public TournamentStandingsDto getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    Tournament tournament = dao.getById(id);
    ParticipantsDto participants = participantsService.getById(id);

    return mapper.entityToStandingsDto(participants, tournament);
  }

  @Override
  public Stream<TournamentListDto> search(TournamentListDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = dao.search(searchParameters);

    return tournaments.stream()
        .map(tournament -> mapper.entityToListDto(tournament));
  }

  @Override
  public TournamentStandingsDto update(long id, TournamentStandingsDto toUpdate) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({}, {})", id, toUpdate);
    validator.validateToUpdate(toUpdate);
    participantsValidator.validateForUpdate(toUpdate.participants());
    ArrayList<HorseParticipantDto> updates = mapper.entityToUpdateStandingsDto(toUpdate);
    HashMap<Long, HorseParticipantDto> updatesMap = new HashMap<>();
    for (HorseParticipantDto horse : updates) {
      updatesMap.put(horse.id(), horse);
    }
    ArrayList<HorseParticipantDto> oldParticipants = participantsService.getById(id).participantsArray();
    updates = new ArrayList<>();
    for (HorseParticipantDto horse : oldParticipants) {
      if (updatesMap.get(horse.id()) != null) {
        LOG.debug("Entrynumber: " + updatesMap.get(horse.id()).entryNumber() + " RR: " + updatesMap.get(horse.id()).roundReached());
        updates.add(new HorseParticipantDto(horse.id(), horse.name(), horse.dateOfBirth(), updatesMap.get(horse.id()).entryNumber(),
            updatesMap.get(horse.id()).roundReached()));
      } else {
        updates.add(new HorseParticipantDto(horse.id(), horse.name(), horse.dateOfBirth(), 0, 0));
      }
    }
    ParticipantsDto participantsDto = participantsService.update(updates, id);
    Tournament tournament = dao.getById(id);

    return mapper.entityToStandingsDto(participantsDto, tournament);
  }
}

