package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
/**
 * DTO class for horses as participants in tournament
 */
public record HorseParticipantDto(Long id, String name, LocalDate dateOfBirth, Integer entryNumber, Integer roundReached) {

}
