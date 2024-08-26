package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {
  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void getByIdRequestReturns200() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/tournaments/-1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    TournamentStandingsDto test = objectMapper.readerFor(TournamentStandingsDto.class).readValue(body);
    final List<HorseParticipantDto> expectedParticipants = new ArrayList<>(Arrays.asList(
      new HorseParticipantDto(-1L, "Wendy", LocalDate.parse("2019-08-05"), 8, 0),
      new HorseParticipantDto(-2L, "Hugo", LocalDate.parse("2020-02-20"), 1, 3),
      new HorseParticipantDto(-3L, "Bella", LocalDate.parse("2005-04-08"), 2, 0),
      new HorseParticipantDto(-4L, "Thunder", LocalDate.parse("2008-07-15"), 3, 1),
      new HorseParticipantDto(-5L, "Luna", LocalDate.parse("2012-11-22"), 4, 0),
      new HorseParticipantDto(-6L, "Apollo", LocalDate.parse("2003-09-03"), 5, 2),
      new HorseParticipantDto(-7L, "Sophie", LocalDate.parse("2010-06-18"), 6, 0),
      new HorseParticipantDto(-8L, "Max", LocalDate.parse("2006-03-27"), 7, 1)
    ));
    assertNotNull(test);
    assertThat(test).isExactlyInstanceOf(TournamentStandingsDto.class);
    assertThat(test.id()).isEqualTo(-1L);
    assertThat(test.name()).isEqualTo("Yearly Tournament");
    assertThat(test.participants()).isEqualTo(expectedParticipants);
  }

}
