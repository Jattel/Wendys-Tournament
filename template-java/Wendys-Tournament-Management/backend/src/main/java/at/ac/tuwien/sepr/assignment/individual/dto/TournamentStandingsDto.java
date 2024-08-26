package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.ArrayList;
/**
 * DTO class for tournament standings
 */
public record TournamentStandingsDto(
    Long id,
    String name,
    ArrayList<HorseParticipantDto> participants,
    TournamentStandingsTreeDto tree
) {
}
