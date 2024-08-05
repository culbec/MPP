package Repository;

import Model.Participant;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for the participant repository.
 */
public interface ParticipantRepository extends Repository<UUID, Participant> {
    /**
     * Finds a participant by the passed fields.
     *
     * @param participant Model.Participant to be found.
     * @return An {@code Optional} containing the participant if the participant has been found.
     */
    Optional<Participant> findParticipantByFields(Participant participant) throws RepositoryException;

    /**
     * Finds all participants by the passed team.
     *
     * @param team Team of the participants.
     * @return An {@code Iterable} containing the participants if the participants have been found.
     */
    Iterable<Participant> findParticipantsByTeam(String team) throws RepositoryException;
}
