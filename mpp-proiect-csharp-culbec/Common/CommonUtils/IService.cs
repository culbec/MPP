using Model;

namespace Common.CommonUtils;

public interface IService
{
    /// <summary>
    /// Finds the participants of the passed team.
    /// </summary>
    /// <param name="team">The team of the participants.</param>
    /// <returns>A collection with the participants given by team.</returns>
    IEnumerable<Participant> FindParticipantsByTeam(string team);

    /// <summary>
    /// Adds a participant to the storage.
    /// </summary>
    /// <param name="firstName">First name of the participant.</param>
    /// <param name="lastName">Last name of the participant.</param>
    /// <param name="team">Team of the participant.</param>
    /// <param name="engineCapacity">Engine capacity that the participant enrolled with.</param>
    void AddParticipant(string firstName, string lastName, string team, int engineCapacity);

    /// <summary>
    /// Finds all the races.
    /// </summary>
    /// <returns>A collection containing all the races.</returns>
    IEnumerable<Race> FindAllRaces();

    /// <summary>
    /// Finds all the race engine capacities.
    /// </summary>
    /// <returns>A collection containing all the race engine capacities.</returns>
    IEnumerable<int> FindAllRaceEngineCapacities();

    /// <summary>
    /// Logs in a user.
    /// </summary>
    /// <param name="username">Username of the user.</param>
    /// <param name="password">Password of the user.</param>
    /// <param name="client">The observer that shall be notified of the user's log in.</param>
    /// <returns>The user with the passed credentials, if it exists.</returns>
    User Login(string username, string password, IObserver client);

    /// <summary>
    /// Logs out a user from the application.
    /// </summary>
    /// <param name="user">User that needs to be logged out.</param>
    /// <param name="client">The observer that shall be notified of the user's log out.</param>
    void Logout(User user, IObserver client);
}