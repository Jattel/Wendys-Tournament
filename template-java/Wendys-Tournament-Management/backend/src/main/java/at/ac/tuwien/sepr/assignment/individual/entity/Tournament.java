package at.ac.tuwien.sepr.assignment.individual.entity;


import java.time.LocalDate;

/**
 * Represents a tournament in the persistent data store.
 */
public class Tournament {
  private Long id;
  private String name;
  private LocalDate start;
  private LocalDate end;

  public Long getId() {
    return id;
  }

  public Tournament setId(Long id) {
    this.id = id;
    return this;
  }

  public Tournament setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public LocalDate getStart() {
    return start;
  }

  public Tournament setStart(LocalDate start) {
    this.start = start;
    return this;
  }

  public LocalDate getEnd() {
    return end;
  }

  public Tournament setEnd(LocalDate end) {

    this.end = end;
    return this;
  }


}
