package Service;

import Repository.RepositoryException;
import Exceptions.ServiceException;
import Model.Race;
import Repository.RaceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ServiceRaceImpl implements ServiceRace {
    private static final Logger logger = LogManager.getLogger();
    private final RaceRepository raceRepository;

    public ServiceRaceImpl(RaceRepository raceRepository) {
        logger.traceEntry("Initializing the ServiceRaceImpl with the RaceRepository...");
        this.raceRepository = raceRepository;
        logger.traceExit("Initialized the ServiceRaceImpl!");
    }

    @Override
    public Iterable<Race> findAllRaces() throws ServiceException {
        logger.traceEntry("Finding all the races...");

        try {
            List<Race> races = (List<Race>) this.raceRepository.findAll();

            logger.traceExit("Found all the races!");
            return races;
        } catch (RepositoryException e) {
            logger.error("Couldn't find all the races: {}", e.getMessage());
            throw new ServiceException("Couldn't find all the races: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Integer> findAllRaceEngineCapacities() throws ServiceException {
        logger.traceEntry("Finding all the race engine capacities...");

        try {
            List<Integer> engineCapacities = (List<Integer>) this.raceRepository.findAllRaceEngineCapacities();

            logger.traceExit("Found all the engine capacities!");
            return engineCapacities;
        } catch (RepositoryException e) {
            logger.error("Couldn't find all the race engine capacities: {}", e.getMessage());
            throw new ServiceException("Couldn't find all the race engine capacities: " + e.getMessage());
        }
    }
}
