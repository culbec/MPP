using Model;

namespace Server.Service;

public interface IServiceParticipant
{
    /// <summary>
    ///     Finds the participants of the passed team.
    /// </summary>
    /// <param name="team">Team of the participants.</param>
    /// <returns>An IEnumerable containing the participants of the passed team.</returns>
    IEnumerable<Participant> FindParticipantsByTeam(string team);

    /// <summary>
    ///     Adds a participant to the repository.
    /// </summary>
    /// <param name="firstName">First name of the participant.</param>
    /// <param name="lastName">Last name of the participant.</param>
    /// <param name="team">Team of the participant.</param>
    /// <param name="engineCapacity">Engine capacity with which the participant enrolled.</param>
    /// <returns>The participant that was added.</returns>
    Participant AddParticipant(string firstName, string lastName, string team, int engineCapacity);
}