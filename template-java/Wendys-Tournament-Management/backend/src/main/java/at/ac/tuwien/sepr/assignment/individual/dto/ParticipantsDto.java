package at.ac.tuwien.sepr.assignment.individual.dto;


import java.util.ArrayList;

/**
 * DTO class for participants as part of a tournament
 */
public record ParticipantsDto(long tournamentId, ArrayList<HorseParticipantDto> participantsArray) {

}


