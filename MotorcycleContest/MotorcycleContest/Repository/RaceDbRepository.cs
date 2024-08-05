using System.Collections.ObjectModel;
using System.Data;
using log4net;
using MotorcycleContest.Domain;
using MotorcycleContest.Exceptions;

namespace MotorcycleContest.Repository;

public class RaceDbRepository : AbstractDbRepository<Guid, Race>, IRaceRepository
{
    private static readonly ILog Log = LogManager.GetLogger("RaceDbRepository");
    public RaceDbRepository(IDictionary<string, string> properties) : base(properties)
    {
        Log.Info("Initializing the RaceDbRepository...");
    }

    public override Race? FindOne(Guid tid)
    {
        Log.InfoFormat("Finding a race with the ID {0}", tid);

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established!");

            Log.Info("Creating a command...");
            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created!");

                Log.Info("Populating the command...");

                command.CommandText = "select rid, engine_capacity, no_participants from races where rid = @rid";
                var paramRid = command.CreateParameter();
                paramRid.ParameterName = "@rid";
                paramRid.Value = tid;
                command.Parameters.Add(paramRid);

                Log.Info("Executing the command...");
                using (var dataReader = command.ExecuteReader())
                {
                    Log.Info("Command executed!");
                    if (!dataReader.Read())
                    {
                        Log.Error("Cannot find a race with the passed ID!");
                        return null;
                    }

                    var race = ExtractFromDataReader(dataReader);
                    Log.InfoFormat("Found a race with the passed ID {0}: {1}", tid, race);
                    return race;
                }
            }
        }
    }

    public override Collection<User>? FindAll()
    {
        return null;
    }

    public override Race? Save(Race te)
    {
        return null;
    }

    public override Race? Delete(Race te)
    {
        return null;
    }

    public override Race? Update(Race te)
    {
        Log.InfoFormat("Updating the race with the ID: {0}", te.Id);

        Log.InfoFormat("Finding the race with the ID: {0}", te.Id);
        var oldRace = FindOne(te.Id);

        if (oldRace == null)
        {
            Log.Error("A race with the same ID as the passed one doesn't exist!");
            return null;
        }

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established!");

            Log.Info("Creating a command...");
            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created!");

                Log.Info("Populating the command!");

                command.CommandText = "update races set no_participants = @noParticipants where rid = @rid";

                var paramNoParticipants = command.CreateParameter();
                paramNoParticipants.ParameterName = "@noParticipants";
                paramNoParticipants.Value = te.NoParticipants;
                command.Parameters.Add(paramNoParticipants);

                var paramRid = command.CreateParameter();
                paramRid.ParameterName = "@rid";
                paramRid.Value = te.Id;
                command.Parameters.Add(paramRid);

                Log.Info("Executing the command...");
                var result = command.ExecuteNonQuery();

                if (result == 0)
                {
                    throw new RepositoryException("Couldn't update the race!");
                }

                Log.Info("Updated the race!");
                return oldRace;
            }

        }
    }

    public Race? FindRaceByEngineCapacity(int engineCapacity)
    {
        Log.InfoFormat("Findind a race by the engine capacity {0}...", engineCapacity);

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established!");

            Log.Info("Creating a command...");
            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created!");

                Log.Info("Populating the command...");

                command.CommandText =
                    "select rid, engine_capacity, no_participants from races r where r.engine_capacity = @engineCapacity";

                var paramEngineCapacity = command.CreateParameter();
                paramEngineCapacity.ParameterName = "@engineCapacity";
                paramEngineCapacity.Value = engineCapacity;
                command.Parameters.Add(paramEngineCapacity);

                Log.Info("Executing the command...");
                using (var dataReader = command.ExecuteReader())
                {
                    if (!dataReader.Read())
                    {
                        Log.Error("No race was found with the passed engine capacity!");
                        return null;
                    }

                    var race = ExtractFromDataReader(dataReader);
                    Log.InfoFormat("Found a race with the passed engine capacity of {0}: {1}", engineCapacity, race);
                    return race;
                }

            }
        }
    }

    protected override Race ExtractFromDataReader(IDataReader dataReader)
    {
        Log.Info("Extracting the race from the data reader...");
        Guid rid = dataReader.GetGuid(0);
        int engineCapacity = dataReader.GetInt32(1);
        int noParticipants = dataReader.GetInt32(2);

        Log.Info("Participant extracted.");
        return new Race(rid, engineCapacity, noParticipants);
    }
}