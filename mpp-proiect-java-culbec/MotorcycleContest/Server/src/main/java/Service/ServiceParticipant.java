package Service;


import Exceptions.ServiceException;
import Model.Participant;

public interface ServiceParticipant {
    /**
     * Finds all participants from a given team.
     *
     * @param team The team to search for.
     * @return An iterable containing all participants from the given team.
     * @throws ServiceException If the team doesn't exist.
     */
    Iterable<Participant> findParticipantsByTeam(String team) throws ServiceException;

    /**
     * Adds a participant to the contest.
     *
     * @param firstName      The first name of the participant.
     * @param lastName       The last name of the participant.
     * @param team           The team of the participant.
     * @param engineCapacity The engine capacity of the participant's motorcycle.
     * @throws ServiceException If the participant already exists.
     */
    Participant addParticipant(String firstName, String lastName, String team, int engineCapacity) throws ServiceException;
}
