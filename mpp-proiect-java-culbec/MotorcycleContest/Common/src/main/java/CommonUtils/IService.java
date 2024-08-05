package CommonUtils;

import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.Participant;
import Model.Race;
import Model.User;

public interface IService {
    /**
     * Finds all participants from a given team.
     *
     * @param team The team to search for.
     * @return An iterable containing all participants from the given team.
     * @throws ServiceException If the team doesn't exist.
     */
    Iterable<Participant> findParticipantsByTeam(String team) throws ServiceException, AppException;

    /**
     * Adds a participant to the contest.
     *
     * @param firstName      The first name of the participant.
     * @param lastName       The last name of the participant.
     * @param team           The team of the participant.
     * @param engineCapacity The engine capacity of the participant's motorcycle.
     * @throws ServiceException If the participant already exists.
     */
    void addParticipant(String firstName, String lastName, String team, int engineCapacity) throws ServiceException, AppException;

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

    /**
     * Logins a user into the app.
     *
     * @param username Username passed by the user.
     * @param password Password passed by the user.
     * @param client Client that requested the login.
     * @return The user with the passed credentials.
     * @throws ServiceException If the user doesn't exist or the password is incorrect.
     */
    User login(String username, String password, Observer client) throws ServiceException, AppException;

    /**
     * Logs out a user from the app.
     * @param user User to be logged out.
     * @param client Client that requested the logout;
     */
    void logout(User user, Observer client) throws ServiceException, AppException;
}
