package at.ac.tuwien.sepr.assignment.individual.dto;
/**
 * DTO class for tournament standings tree
 */
public record TournamentStandingsTreeDto(
    HorseParticipantDto thisParticipant,
    TournamentStandingsTreeDto[] branches
) {
}
