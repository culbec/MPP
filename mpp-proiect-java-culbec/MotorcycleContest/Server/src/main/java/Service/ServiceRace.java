package Service;


import Exceptions.ServiceException;
import Model.Race;

public interface ServiceRace {
    /**
     * Finds all the races.
     *
     * @return An {@code Iterable} containing all the races.
     * @throws ServiceException If something went wrong in searching the races.
     */
    Iterable<Race> findAllRaces() throws ServiceException;

    /**
     * Finds all engine capacities of the saved races.
     *
     * @return An {@code Iterable} containing all the engine capacities of the races.
     * @throws ServiceException If something went wrong in searching the engine capacities.
     */

    Iterable<Integer> findAllRaceEngineCapacities() throws ServiceException;
}
