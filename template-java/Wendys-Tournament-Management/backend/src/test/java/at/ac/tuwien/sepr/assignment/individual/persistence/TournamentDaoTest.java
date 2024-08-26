package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class TournamentDaoTest extends TestBase {
  @Autowired
  TournamentDao tournamentDao;

  @Test
  public void throwNotFoundExceptionWithGetTournamentById() {
    assertThrows(NotFoundException.class, () -> {
      Tournament update = tournamentDao.getById(-50L);
    });
  }

  @Test
  public void getTournamentById() throws NotFoundException {
    Tournament tournament = tournamentDao.getById(-1L);

    assertNotNull(tournament);
    assertThat(tournament).isExactlyInstanceOf(Tournament.class);
    assertThat(tournament.getId()).isEqualTo(-1L);
    assertThat(tournament.getName()).isEqualTo("Yearly Tournament");
    assertThat(tournament.getStart()).isEqualTo(LocalDate.of(2024, 01, 04));
    assertThat(tournament.getEnd()).isEqualTo(LocalDate.of(2024, 02, 01));
  }
}
