using Common.CommonUtils;
using Common.Exceptions;
using Model;

namespace Server.Service;

public interface IServiceRace
{
    /// <summary>
    ///     Finds all the races.
    /// </summary>
    /// <returns>An IEnumerable containing all the races.</returns>
    /// <exception cref="ServiceException">If the retrieval of the races failed.</exception>
    IEnumerable<Race> FindAllRaces();

    /// <summary>
    ///     Finds all the race engine capacities saved.
    /// </summary>
    /// <returns>An IEnumerable containing all the race engine capacities.</returns>
    /// <exception cref="ServiceException">If the retrieval of the engine capacities failed.</exception>
    IEnumerable<int> FindAllRaceEngineCapacities();
}