package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;

import java.util.Collection;
import java.util.Set;

public interface BreedDao {
  /**
   * Get all breed from the persistent data store.
   *
   * @return the all breeds
   */
  Collection<Breed> allBreeds();

  /**
   * Get a breed by its ID from the persistent data store.
   *
   * @param breedIds the ID of the breed to get
   * @return the Breed
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Get the breeds that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in breed.
   *
   * @param searchParams the parameters to use in searching.
   * @return the breed where all given parameters match.
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
