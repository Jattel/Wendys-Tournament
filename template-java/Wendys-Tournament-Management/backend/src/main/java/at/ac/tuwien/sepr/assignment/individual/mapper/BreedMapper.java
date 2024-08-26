package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.springframework.stereotype.Component;

@Component
public class BreedMapper {
  /**
   * Convert a breed entity object to a {@link BreedDto}.
   *
   * @param breed the breed to convert
   * @return the converted {@link BreedDto}
   */
  public BreedDto entityToDto(Breed breed) {
    return new BreedDto(breed.getId(), breed.getName());
  }
}
