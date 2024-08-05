using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public interface IRaceRepository : IRepository<Guid, Race>
{
    /**
     * Finds a race by the passed engine capacity.
     * @param engineCapacity Engine capacity of the race.
     * @return The race with the respective engine capacity, or null if it doesn't exist.
     */
    Race? FindRaceByEngineCapacity(int engineCapacity);
}