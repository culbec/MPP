using System.Data;
using MySql.Data.MySqlClient;

namespace Persistence.ConnectionUtils;

public class MariaDbConnectionFactory : ConnectionFactory
{
    public override IDbConnection CreateConnection(IDictionary<string, string> properties)
    {
        var connectionString = properties["ConnectionString"];
        return new MySqlConnection(connectionString);
    }
}