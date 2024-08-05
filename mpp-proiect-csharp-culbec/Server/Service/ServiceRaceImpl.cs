using Common.Exceptions;
using log4net;
using Model;
using Persistence.Repository;

namespace Server.Service;

public class ServiceRaceImpl : IServiceRace
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ServiceRaceImpl));
    private readonly IRepositoryRace _repositoryRace;

    public ServiceRaceImpl(IRepositoryRace repositoryRace)
    {
        Log.Info("Initializing ServiceRaceImpl.");
        _repositoryRace = repositoryRace;
        Log.Info("ServiceRaceImpl initialized.");
    }

    public IEnumerable<Race> FindAllRaces()
    {
        Log.Info("Finding all the races stored in the repository...");

        try
        {
            var races = _repositoryRace.FindAll()!.ToList();

            Log.InfoFormat("Found {0} races in the repository", races.Count);
            return races;
        }
        catch (RepositoryException e)
        {
            Log.ErrorFormat("Something went wrong while retrieving all the races: {0}", e.Message);
            throw new ServiceException("Something went wrong while retrieving all the races: " + e.Message);
        }
    }

    public IEnumerable<int> FindAllRaceEngineCapacities()
    {
        Log.Info("Finding all the race engine capacities...");

        try
        {
            var engineCapacities = _repositoryRace.FindAllRaceEngineCapacities().ToList();

            Log.InfoFormat("Found {0} engine capacities in the repository", engineCapacities.Count);
            return engineCapacities;
        }
        catch (RepositoryException e)
        {
            Log.ErrorFormat("Something went wrong while retrieving all the engine capacities: {0}", e.Message);
            throw new ServiceException("Something went wrong while retrieving all the engine capacities: " + e.Message);
        }
    }
}