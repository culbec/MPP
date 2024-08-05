package Service;

import Exceptions.ServiceException;
import Model.Participant;
import Repository.ParticipantRepository;
import Repository.RepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceParticipantImpl implements ServiceParticipant {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ParticipantRepository participantRepository;

    public ServiceParticipantImpl(ParticipantRepository participantRepository) {
        logger.traceEntry("Initializing the ServiceParticipantImpl with the ParticipantRepository...");
        this.participantRepository = participantRepository;
        logger.traceExit("Initialized the ServiceParticipantImpl with the ParticipantRepository!");
    }


    @Override
    public Iterable<Participant> findParticipantsByTeam(String team) throws ServiceException {
        logger.traceEntry("Finding all participants from the team {}...", team);

        try {
            List<Participant> participants = (List<Participant>) participantRepository.findParticipantsByTeam(team);

            if (participants.isEmpty()) {
                logger.error("The team {} doesn't exist!", team);
                throw new ServiceException("The team doesn't exist!");
            }

            logger.traceExit("Found all participants from the team {}!", team);
            return participants;
        } catch (RepositoryException e) {
            logger.error("Couldn't find the participants from the team {}: {}", team, e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Participant addParticipant(String firstName, String lastName, String team, int engineCapacity) throws ServiceException {
        logger.traceEntry("Adding the participant with the first name {}, last name {}, team {} and engine capacity {}...", firstName, lastName, team, engineCapacity);

        logger.info("Finding the participant by the fields {}, {}, {}, {}", firstName, lastName, team, engineCapacity);
        Participant participant = new Participant.Builder()
                .setId(UUID.randomUUID())
                .setFirstName(firstName)
                .setLastName(lastName)
                .setTeam(team)
                .setEngineCapacity(engineCapacity)
                .build();

        try {
            Optional<Participant> saved = this.participantRepository.save(participant);

            if (saved.isEmpty()) {
                logger.error("The participant with the first name {}, last name {}, team {} and engine capacity {} already exists!", firstName, lastName, team, engineCapacity);
                throw new ServiceException("The participant already exists!");
            }

            logger.traceExit("The participant was saved. Returning...");
            return saved.get();
        } catch (RepositoryException e) {
            logger.error("Something wrong happened when trying to find the participant by the fields: {}", e.getMessage());
            throw new ServiceException("Something wrong happened when trying to find the participant by the fields:" + e.getMessage());
        }
    }
}
