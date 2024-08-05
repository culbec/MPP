using System.Data;
using Model;
using Persistence.ConnectionUtils;

namespace Persistence.Repository;

public class RepositoryParticipantDb : AbstractDbRepository<Guid, Participant>, IRepositoryParticipant
{
    public RepositoryParticipantDb(IDictionary<string, string> properties) : base(properties)
    {
        Log.Info("Initializing the ParticipantDbRepository...");
    }

    public override Participant? FindOne(Guid tid)
    {
        return null;
    }

    public override IEnumerable<Participant>? FindAll()
    {
        return null;
    }

    public override Participant? Save(Participant te)
    {
        Log.InfoFormat("Saving participant {0}", te);

        Log.Info("Verifying if the participant already exists...");
        var existingParticipant = FindParticipantByFields(te);
        if (existingParticipant != null)
        {
            Log.Error("Participant already exists.");
            return existingParticipant;
        }

        try
        {
            using var connection = DBUtils.GetConnection(Properties);
            Log.Info("Connection established.");

            using var command = connection.CreateCommand();
            command.CommandText =
                "INSERT INTO participants (first_name, last_name, engine_capacity, team) VALUES (@firstName, @lastName, @engineCapacity, @team)";

            var paramFirstName = command.CreateParameter();
            paramFirstName.ParameterName = "@firstName";
            paramFirstName.Value = te.FirstName;
            command.Parameters.Add(paramFirstName);

            var paramLastName = command.CreateParameter();
            paramLastName.ParameterName = "@lastName";
            paramLastName.Value = te.LastName;
            command.Parameters.Add(paramLastName);

            var paramEngineCapacity = command.CreateParameter();
            paramEngineCapacity.ParameterName = "@engineCapacity";
            paramEngineCapacity.Value = te.EngineCapacity;
            command.Parameters.Add(paramEngineCapacity);

            var paramTeam = command.CreateParameter();
            paramTeam.ParameterName = "@team";
            paramTeam.Value = te.Team;
            command.Parameters.Add(paramTeam);

            Log.Info("Executing the command...");

            var result = command.ExecuteNonQuery();
            Log.InfoFormat("Executed the command. {0} rows affected.", result);

            return null;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new RepositoryException(e.Message);
        }
    }

    public override Participant? Delete(Participant te)
    {
        return null;
    }

    public override Participant? Update(Participant te)
    {
        return null;
    }

    public Participant? FindParticipantByFields(Participant participant)
    {
        Log.InfoFormat("Finding participant with fields {0}", participant);

        try
        {
            using var connection = DBUtils.GetConnection(Properties);
            Log.Info("Connection established.");

            Log.Info("Creating a command...");
            using var command = connection.CreateCommand();
            Log.Info("Command created.");

            command.CommandText =
                "select pid, first_name, last_name, engine_capacity, team from participants where first_name = @FirstName and last_name = @LastName and team = @Team and engine_capacity = @EngineCapacity";

            var paramFirstName = command.CreateParameter();
            paramFirstName.ParameterName = "@FirstName";
            paramFirstName.Value = participant.FirstName;
            command.Parameters.Add(paramFirstName);

            var paramLastName = command.CreateParameter();
            paramLastName.ParameterName = "@LastName";
            paramLastName.Value = participant.LastName;
            command.Parameters.Add(paramLastName);

            var paramTeam = command.CreateParameter();
            paramTeam.ParameterName = "@Team";
            paramTeam.Value = participant.Team;
            command.Parameters.Add(paramTeam);

            var paramEngineCapacity = command.CreateParameter();
            paramEngineCapacity.ParameterName = "@EngineCapacity";
            paramEngineCapacity.Value = participant.EngineCapacity;
            command.Parameters.Add(paramEngineCapacity);

            Log.Info("Executing the command...");
            using var dataReader = command.ExecuteReader();
            Log.Info("Command executed.");
            if (!dataReader.Read())
            {
                Log.Error("No participant found.");
                return null;
            }

            Log.Info("Participant found.");
            return ExtractFromDataReader(dataReader);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new RepositoryException(e.Message);
        }
    }

    public IEnumerable<Participant> FindParticipantsByTeam(string team)
    {
        Log.InfoFormat("Finding all the participants of the team {0}", team);

        try
        {
            using var connection = DBUtils.GetConnection(Properties);
            Log.Info("Connection established.");

            Log.Info("Creating a command...");
            using var command = connection.CreateCommand();
            Log.Info("Command created.");

            Log.Info("Populating the command...");
            command.CommandText =
                "select pid, first_name, last_name, engine_capacity, team from participants where team = @team";

            var paramTeam = command.CreateParameter();
            paramTeam.ParameterName = "@team";
            paramTeam.Value = team;
            command.Parameters.Add(paramTeam);

            Log.Info("Executing the command...");
            using var dataReader = command.ExecuteReader();
            Log.Info("Command executed.");

            var participants = new HashSet<Participant>();
            Log.Info("Reading the data...");
            while (dataReader.Read()) participants.Add(ExtractFromDataReader(dataReader));

            if (participants.Count == 0)
                Log.Warn("No participants found.");
            else
                Log.Info("Participants found.");
            return participants;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new RepositoryException(e.Message);
        }
    }

    protected override Participant ExtractFromDataReader(IDataReader dataReader)
    {
        Log.Info("Extracting participant from data reader...");
        var id = Guid.NewGuid();
        var firstName = dataReader.GetString(1);
        var lastName = dataReader.GetString(2);
        var engineCapacity = dataReader.GetInt32(3);
        var team = dataReader.GetString(4);

        Log.Info("Participant extracted.");
        return Participant.NewBuilder()
            .SetId(id)
            .SetFirstName(firstName)
            .SetLastName(lastName)
            .SetEngineCapacity(engineCapacity)
            .SetTeam(team)
            .Build();
    }
}