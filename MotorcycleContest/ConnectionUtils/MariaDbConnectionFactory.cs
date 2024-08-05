using System.Data;
using MySql.Data.MySqlClient;

namespace ConnectionUtils;

public class MariaDbConnectionFactory : ConnectionFactory
{
    public override IDbConnection CreateConnection(IDictionary<string, string> properties)
    {
        // MariaDB Connection
        // const string connectionString = "Database=MotorcycleContest;" +
        //                                 "Data Source=localhost;" +
        //                                 "User id=mariadb;" +
        //                                 "Password=mariadb;";
        var connectionString = properties["ConnectionString"];
        return new MySqlConnection(connectionString);
    }
}