using System.Collections.Concurrent;
using Common.CommonUtils;
using Common.Exceptions;
using Model;
using log4net;

namespace Server.Service;

public class ServiceImpl : IService
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ServiceImpl));
    private readonly IServiceParticipant _serviceParticipant;
    private readonly IServiceUser _serviceUser;
    private readonly IServiceRace _serviceRace;
    private readonly ConcurrentDictionary<string, IObserver> _loggedClients = new();

    public ServiceImpl(IServiceParticipant serviceParticipant, IServiceUser serviceUser, IServiceRace serviceRace)
    {
        _serviceParticipant = serviceParticipant;
        _serviceUser = serviceUser;
        _serviceRace = serviceRace;
    }

    public IEnumerable<Participant> FindParticipantsByTeam(string team)
    {
        Log.InfoFormat("Finding the participants by team: {0}", team);

        try
        {
            var participants = _serviceParticipant.FindParticipantsByTeam(team).ToList();
            Log.InfoFormat("Found {0} participants", participants.Count);

            return participants;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Could not find the participants by team: {0}", team);
            throw new ServiceException("Could not find the participants by team: " + e.Message);
        }
    }

    public void AddParticipant(string firstName, string lastName, string team, int engineCapacity)
    {
        Log.InfoFormat("Trying to add the participants with fields: {0}, {1}, {2}, {3}", firstName, lastName, team,
            engineCapacity);

        try
        {
            var participant = _serviceParticipant.AddParticipant(firstName, lastName, team, engineCapacity);
            Log.InfoFormat("Participant added: {0}", participant);
            NotifyParticipantAdded(participant);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Could not add the participant: {0}", e.Message);
            throw new ServiceException("Could not add the participant: " + e.Message);
        }
    }

    public IEnumerable<Race> FindAllRaces()
    {
        Log.Info("Finding all the races...");

        try
        {
            var races = this._serviceRace.FindAllRaces().ToList();
            Log.InfoFormat("Found {0} races", races.Count);

            return races;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Could not find all the races: {0}", e.Message);
            throw new ServiceException("Could not find all the races: " + e.Message);
        }
    }

    public IEnumerable<int> FindAllRaceEngineCapacities()
    {
        Log.InfoFormat("Finding all the race engine capacities...");

        try
        {
            var engineCapacities = _serviceRace.FindAllRaceEngineCapacities().ToList();
            Log.InfoFormat("Found {0} engine capacities", engineCapacities.Count);

            return engineCapacities;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Could not find all the race engine capacities: {0}", e.Message);
            throw new ServiceException("Could not find all the race engine capacities: " + e.Message);
        }

    }

    public User Login(string username, string password, IObserver client)
    {
        Log.InfoFormat("Trying to login the user: {0}", username);

        Log.Info("Verifying if the user is already logged in...");
        if (_loggedClients.ContainsKey(username))
        {
            Log.Error("The user is already logged in!");
            throw new ServiceException("The user is already logged in!");
        }

        try
        {
            Log.Info("Trying to login the user...");
            var user = _serviceUser.Login(username, password);

            Log.Info("User logged in successfully.");
            _loggedClients[username] = client;

            return user;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Could not login the user: {0}", e.Message);
            throw new ServiceException("Could not login the user: " + e.Message);
        }
    }

    public void Logout(User user, IObserver client)
    {
        Log.InfoFormat("Logging out the user: {0}", user);

        _loggedClients.TryRemove(user.Username!, out var localClient);

        if (localClient == null)
        {
            Log.Error("The user is not logged in!");
            throw new ServiceException("The user is not logged in!");
        }

        Log.Info("The user was logged out.");
    }

    private void NotifyParticipantAdded(Participant participant)
    {
        foreach (var client in _loggedClients.Values)
        {
            Task.Run(() => client.ParticipantAdded(participant));
        }
    }
}