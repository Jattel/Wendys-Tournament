package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class TournamentServiceTest extends TestBase {
  @Autowired
  TournamentService tournamentService;

  @Test
  public void throwNotFoundExceptionWithInvalidGetById()  {
    assertThrows(NotFoundException.class, () -> {
      TournamentStandingsDto create = tournamentService.getById(-56L);
    });
  }
}
