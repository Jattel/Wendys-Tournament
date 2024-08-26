package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DTO class for tournament in details
 */
public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    ArrayList<HorseParticipantDto> participants) {
  public TournamentDetailDto withId(long newId) {
    return new TournamentDetailDto(
        newId,
        name,
        startDate,
        endDate,
        participants);
  }
}
