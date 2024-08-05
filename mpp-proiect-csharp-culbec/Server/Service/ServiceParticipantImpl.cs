using Common.Exceptions;
using log4net;
using Model;
using Persistence.Repository;

namespace Server.Service;

public class ServiceParticipantImpl : IServiceParticipant
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ServiceParticipantImpl));
    private readonly IRepositoryParticipant _repositoryParticipant;

    public ServiceParticipantImpl(IRepositoryParticipant repositoryParticipant)
    {
        Log.Info("Initializing ServiceParticipantImpl.");
        _repositoryParticipant = repositoryParticipant;
        Log.Info("ServiceParticipantImpl initialized.");
    }

    public IEnumerable<Participant> FindParticipantsByTeam(string team)
    {
        Log.InfoFormat("Searching for participants of team {0}.", team);
        try
        {
            var participants = _repositoryParticipant.FindParticipantsByTeam(team).ToList();

            Log.InfoFormat("Found {0} participants of team {1}.", participants.Count, team);
            return participants;
        }
        catch (RepositoryException e)
        {
            Log.ErrorFormat("Something went wrong when searching for the participants of a team: {0}", e.Message);
            throw new ServiceException("Something went wrong when searching for the participants of a team: " +
                                       e.Message);
        }
    }

    public Participant AddParticipant(string firstName, string lastName, string team, int engineCapacity)
    {
        Log.InfoFormat("Adding participant {0} {1} to team {2} with engine capacity {3}.", firstName, lastName, team,
            engineCapacity);

        try
        {
            Log.Info("Creating the participant...");
            var participant = Participant.NewBuilder()
                .SetId(Guid.NewGuid())
                .SetFirstName(firstName)
                .SetLastName(lastName)
                .SetTeam(team)
                .SetEngineCapacity(engineCapacity)
                .Build();
            Log.Info("Participant created.");

            Log.Info("Attempting to add the participant to the repository...");
            var result = _repositoryParticipant.Save(participant);

            if (result != null)
            {
                Log.Error("The participant already exists!");
                throw new ServiceException("The participant already exists!");
            }

            Log.Info("Participant added successfully.");
            return participant;
        }
        catch (RepositoryException e)
        {
            Log.ErrorFormat("Something went wrong when trying to add a participant: {0}", e.Message);
            throw new ServiceException("Something went wrong when trying to add a participant: " + e.Message);
        }
    }
}