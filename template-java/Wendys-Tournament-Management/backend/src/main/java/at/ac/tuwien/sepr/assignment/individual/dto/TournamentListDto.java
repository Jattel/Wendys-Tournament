package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
/**
 * DTO class for list of tournament in search view.
 */
public record TournamentListDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate) {
}
