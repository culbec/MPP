using Model;

namespace Persistence.Repository;

public interface IRepositoryParticipant : IRepository<Guid, Participant>
{
    /// <summary>
    ///     Finds a participant by the passed fields.
    /// </summary>
    /// <param name="participant">Participant to be found.</param>
    /// <returns>The found participant or null if the participant doesn't exist.</returns>
    Participant? FindParticipantByFields(Participant participant);

    /// <summary>
    ///     Finds all participants by the passed team.
    /// </summary>
    /// <param name="team">Team of the participants.</param>
    /// <returns>An IEnumerable containing the participants, or null if there are no participants of that team.</returns>
    IEnumerable<Participant> FindParticipantsByTeam(string team);
}