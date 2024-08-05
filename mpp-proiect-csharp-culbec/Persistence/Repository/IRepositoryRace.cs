using Model;

namespace Persistence.Repository;

public interface IRepositoryRace : IRepository<int, Race>
{
    /// <summary>
    ///     Finds all engine capacities of the saved races.
    /// </summary>
    /// <returns>An IEnumerable containing all the engine capacities of the races.</returns>
    IEnumerable<int> FindAllRaceEngineCapacities();
}