using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public interface IParticipantRepository : IRepository<Guid, Participant>
{
    /**
     * Find a participant by its fields.
     * @param participant the participant to find.
     * @return the participant with the given fields or null if there is no participant with the given fields.
     */
    Participant? FindParticipantByFields(Participant participant);

    /**
     * Find all participants by team.
     * @param team the team of the participants to find.
     * @return the participants with the given team or null if there are no participants with the given team.
     */
    IEnumerable<Participant>? FindParticipantsByTeam(string team);

    /**
    * Counts the participants by the passed engine capacity.
    * @param engineCapacity Engine capacity of the participants.
    * @return The number of participants with the passed engine capacity.
    */
    int countParticipantsByEngineCapacity(int engineCapacity);
}