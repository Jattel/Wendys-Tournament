package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";
  private final TournamentService service;


  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }
  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public TournamentStandingsDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public TournamentStandingsDto update(@PathVariable("id") long id, @RequestBody TournamentStandingsDto toUpdate)  {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(id, toUpdate);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Tournament to create is not valid", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Participant does not exist in persistent layer.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Stream<TournamentListDto> searchHorses(TournamentListDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TournamentDetailDto post(@RequestBody TournamentDetailDto tournament) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", tournament);
    try {
      return service.create(tournament);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Tournament to create is not valid", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status,  "Tournament not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Tournament's breed does not exist in the persistent layer.", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
