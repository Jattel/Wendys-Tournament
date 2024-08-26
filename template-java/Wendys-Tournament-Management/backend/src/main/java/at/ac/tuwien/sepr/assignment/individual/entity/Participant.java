package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents a horse participant in the persistent data store.
 */
public class Participant {
  private long horseId;
  private long tournamentId;
  private int entryNumber;
  private int roundReached;


  public int getEntryNumber() {
    return entryNumber;
  }

  public Participant setEntryNumber(int entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  public int getRoundReached() {
    return roundReached;
  }

  public Participant setRoundReached(int roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  public long getHorseId() {
    return horseId;
  }

  public Participant setHorseId(long horseId) {
    this.horseId = horseId;
    return this;
  }


  public long getTournamentId() {
    return tournamentId;
  }

  public Participant setTournamentId(long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }
}

