using System.Collections.ObjectModel;
using System.Data;
using log4net;
using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public class ParticipantDbRepository : AbstractDbRepository<Guid, Participant>, IParticipantRepository
{
    private static readonly ILog Log = LogManager.GetLogger("ParticipantDbRepository");

    public ParticipantDbRepository(IDictionary<string, string> properties) : base(properties)
    {
        Log.Info("Initializing the ParticipantDbRepository...");
    }

    public override Participant? FindOne(Guid tid)
    {
        return null;
    }

    public override Collection<User>? FindAll()
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

        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established.");
            using (var command = connection.CreateCommand())
            {
                command.CommandText =
                    "INSERT INTO participants (pid, first_name, last_name, engine_capacity, team) VALUES (@id, @firstName, @lastName, @engineCapacity, @team)";

                var paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = te.Id;
                command.Parameters.Add(paramId);

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
            }
        }

        return null;
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

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established.");
            Log.Info("Creating a command...");
            using (var command = connection.CreateCommand())
            {
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
                using (var dataReader = command.ExecuteReader())
                {
                    Log.Info("Command executed.");
                    if (!dataReader.Read())
                    {
                        Log.Error("No participant found.");
                        return null;
                    }

                    Log.Info("Participant found.");
                    return ExtractFromDataReader(dataReader);
                }
            }
        }
    }

    public IEnumerable<Participant>? FindParticipantsByTeam(string team)
    {
        Log.InfoFormat("Finding all the participants of the team {0}", team);

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established.");
            Log.Info("Creating a command...");

            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created.");

                Log.Info("Populating the command...");
                command.CommandText =
                    "select pid, first_name, last_name, engine_capacity, team from participants where team = @team";

                var paramTeam = command.CreateParameter();
                paramTeam.ParameterName = "@team";
                paramTeam.Value = team;
                command.Parameters.Add(paramTeam);

                Log.Info("Executing the command...");
                using (var dataReader = command.ExecuteReader())
                {
                    Log.Info("Command executed.");
                    var participants = new Collection<Participant>();

                    Log.Info("Reading the data...");
                    while (dataReader.Read())
                    {
                        participants.Add(ExtractFromDataReader(dataReader));
                    }

                    if (participants.Count == 0)
                    {
                        Log.Error("No participants found.");
                        return null;
                    }

                    Log.Info("Participants found.");
                    return participants;
                }
            }
        }
    }

    public int countParticipantsByEngineCapacity(int engineCapacity)
    {
        Log.InfoFormat("Counting the participants by the engine capacity {0}", engineCapacity);

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established.");
            Log.Info("Creating a command...");

            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created.");

                Log.Info("Populating the command...");
                command.CommandText =
                    "select count(*) from participants where engine_capacity = @engineCapacity";

                var paramEngineCapacity = command.CreateParameter();
                paramEngineCapacity.ParameterName = "@engineCapacity";
                paramEngineCapacity.Value = engineCapacity;
                command.Parameters.Add(paramEngineCapacity);

                Log.Info("Executing the command...");

                var result = command.ExecuteScalar();
                if (result == null)
                {
                    Log.Error("No participants found.");
                    return 0;
                }

                Log.InfoFormat("Executed the command. {0} participants found.", result);
                return int.Parse(result.ToString()!);
            }
        }
    }

    protected override Participant ExtractFromDataReader(IDataReader dataReader)
    {
        Log.Info("Extracting participant from data reader...");
        var id = dataReader.GetGuid(0);
        var firstName = dataReader.GetString(1);
        var lastName = dataReader.GetString(2);
        var engineCapacity = dataReader.GetInt32(3);
        var team = dataReader.GetString(4);

        Log.Info("Participant extracted.");
        return new Participant(id, firstName, lastName, team, engineCapacity);
    }
}