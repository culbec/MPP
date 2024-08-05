using System.Data;
using Model;
using Persistence.ConnectionUtils;

namespace Persistence.Repository;

public class RepositoryRaceDb : AbstractDbRepository<int, Race>, IRepositoryRace
{
    public RepositoryRaceDb(IDictionary<string, string> properties) : base(properties)
    {
        Log.Info("Initializing the RaceDbRepository...");
    }

    public override Race? FindOne(int tid)
    {
        return null;
    }

    public override IEnumerable<Race> FindAll()
    {
        Log.Info("Retrieving all the races.");

        try
        {
            using var connection = DBUtils.GetConnection(Properties);
            Log.Info("Connection established.");

            using var command = connection.CreateCommand();
            Log.Info("Command created. Populating the command...");

            command.CommandText =
                "select r.rid, r.engine_capacity, count(p.engine_capacity) as no_participants from races r left join participants p on r.engine_capacity = p.engine_capacity group by r.engine_capacity order by r.engine_capacity";

            Log.Info("Executing the command...");
            using var dataReader = command.ExecuteReader();

            Log.Info("Command executed. Retrieving the result...");

            List<Race> races = [];
            while (dataReader.Read())
            {
                var race = ExtractFromDataReader(dataReader);
                races.Add(race);
            }

            Log.Info("Retrieved all the races. Returning...");
            return races;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new RepositoryException(e.Message);
        }
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
        return null;
    }

    public IEnumerable<int> FindAllRaceEngineCapacities()
    {
        Log.Info("Retrieving all the engine capacities.");

        try
        {
            using var connection = DBUtils.GetConnection(Properties);
            Log.Info("Connection established.");

            using var command = connection.CreateCommand();
            Log.Info("Command created. Populating the command...");

            command.CommandText = "select r.engine_capacity from races r order by r.engine_capacity";

            Log.Info("Executing the command...");
            using var dataReader = command.ExecuteReader();
            Log.Info("Command executed. Retrieving the result...");

            List<int> engineCapacities = [];
            while (dataReader.Read())
            {
                var engineCapacity = dataReader.GetInt32(0);
                engineCapacities.Add(engineCapacity);
            }

            Log.Info("Retrieved all the engine capacities. Returning...");
            return engineCapacities;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new RepositoryException(e.Message);
        }
    }

    protected override Race ExtractFromDataReader(IDataReader dataReader)
    {
        Log.Info("Extracting the race from the data reader...");
        var rid = dataReader.GetInt32(0);
        var engineCapacity = dataReader.GetInt32(1);
        var noParticipants = dataReader.GetInt32(2);

        Log.Info("Participant extracted.");
        return Race.NewBuilder()
            .SetId(rid)
            .SetEngineCapacity(engineCapacity)
            .SetNoParticipants(noParticipants)
            .Build();
    }
}