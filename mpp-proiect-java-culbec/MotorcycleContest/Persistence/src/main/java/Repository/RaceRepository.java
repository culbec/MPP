package Repository;

import Model.Race;

public interface RaceRepository extends Repository<Integer, Race> {
    /**
     * Finds all engine capacities of the saved races.
     *
     * @return An {@code Iterable} containing all the engine capacities of the races.
     */
    Iterable<Integer> findAllRaceEngineCapacities() throws RepositoryException;
}
