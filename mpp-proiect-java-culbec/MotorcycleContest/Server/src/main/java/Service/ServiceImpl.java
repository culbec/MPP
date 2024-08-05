package Service;

import CommonUtils.IService;
import CommonUtils.Observer;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.Participant;
import Model.Race;
import Model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceImpl implements IService {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ServiceUser serviceUser;
    private final ServiceParticipant serviceParticipant;
    private final ServiceRace serviceRace;
    private final Map<String, Observer> loggedClients = new ConcurrentHashMap<>();

    private static final int DEFAULT_THREADS_NO = 3;

    public ServiceImpl(ServiceUser serviceUser, ServiceParticipant serviceParticipant, ServiceRace serviceRace) {
        logger.traceEntry("Initializing the Service...");
        this.serviceUser = serviceUser;
        this.serviceParticipant = serviceParticipant;
        this.serviceRace = serviceRace;
        logger.traceExit("Initialized the Service!");
    }

    @Override
    public Iterable<Participant> findParticipantsByTeam(String team) throws ServiceException {
        logger.traceEntry("Finding the participants from team: {}", team);

        try {
            List<Participant> participants = (List<Participant>) this.serviceParticipant.findParticipantsByTeam(team);
            logger.info("Found {} of team: {}", String.valueOf(participants.size()), team);

            return participants;
        } catch (ServiceException e) {
            logger.error("Couldn't find the participants by team: {}", e.getMessage());
            throw new ServiceException("Couldn't find the participants by team: " + e.getMessage());
        }
    }

    @Override
    public void addParticipant(String firstName, String lastName, String team, int engineCapacity) throws ServiceException {
        logger.traceEntry("Trying to add the participant with fields {}, {}, {}, {}", firstName, lastName, team, engineCapacity);

        try {
            Participant participant = this.serviceParticipant.addParticipant(firstName, lastName, team, engineCapacity);
            logger.info("Successfully added the participant!");

            logger.info("Notifying the clients...");
            this.notifyParticipantAdded(participant);

            logger.traceExit("Notified all the clients!");
        } catch (ServiceException e) {
            logger.error("Couldn't add the participant: {}", e.getMessage());
            throw new ServiceException("Couldn't add the participant: " + e.getMessage());
        }
    }

    /**
     * Notifies the logged in clients that a new participant has been added.
     *
     * @param participant Participant that was added.
     */
    private void notifyParticipantAdded(Participant participant) {
        try (ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS_NO)) {
            this.loggedClients.values().forEach(client -> executor.execute(() -> {
                try {
                    client.participantAdded(participant);
                } catch (AppException e) {
                    logger.error("Couldn't notify the client: {}", e.getMessage());
                }
            }));
        }
    }

    @Override
    public Iterable<Race> findAllRaces() throws ServiceException {
        logger.traceEntry("Finding all the races...");

        try {
            Iterable<Race> races = this.serviceRace.findAllRaces();
            logger.traceExit("Found all the races! Returning...");

            return races;
        } catch (ServiceException e) {
            logger.error("Couldn't find all the races: {}", e.getMessage());
            throw new ServiceException("Couldn't all the races: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Integer> findAllRaceEngineCapacities() throws ServiceException {
        logger.traceEntry("Finding all the engine capacities...");

        try {
            Iterable<Integer> engineCapacities = this.serviceRace.findAllRaceEngineCapacities();
            logger.traceExit("Found all the engine capacities! Returning...");

            return engineCapacities;
        } catch (ServiceException e) {
            logger.error("Couldn't find all the engine capacities: {}", e.getMessage());
            throw new ServiceException("Couldn't find all the engine capacities: " + e.getMessage());
        }
    }

    @Override
    public synchronized User login(String username, String password, Observer client) throws ServiceException {
        logger.traceEntry("Trying to login the user {}", username);

        logger.info("Verifying if the user is already logged in...");
        if (loggedClients.get(username) != null) {
            logger.error("The client is already logged in!");
            throw new ServiceException("The client is already logged in!");
        }

        try {
            logger.info("Trying to retrieve the user by the passed credentials...");
            User user = this.serviceUser.login(username, password);

            logger.info("User found. Updating the logged in clients...");
            loggedClients.put(username, client);

            logger.traceExit("Returning the user...");
            return user;
        } catch (ServiceException e) {
            logger.error("Couldn't login the user: {}", e.getMessage());
            throw new ServiceException("Couldn't login the user: " + e.getMessage());
        }
    }

    @Override
    public synchronized void logout(User user, Observer client) throws ServiceException {
        logger.traceEntry("Logging out the user: {}", user);

        Observer localClient = this.loggedClients.remove(user.getUsername());
        if (localClient == null) {
            logger.error("The user is not logged in!");
            throw new ServiceException("The user is not logged in!");
        }

        logger.traceExit("The user was logged out.");
    }
}
