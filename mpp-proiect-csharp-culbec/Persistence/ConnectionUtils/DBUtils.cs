using System.Data;

namespace Persistence.ConnectionUtils;

public class DBUtils
{
    private static IDbConnection? _instance;

    public static IDbConnection GetConnection(IDictionary<string, string> props)
    {
        if (_instance != null && _instance.State != ConnectionState.Closed) return _instance;
        _instance = getNewConnection(props);
        _instance.Open();

        return _instance;
    }

    private static IDbConnection getNewConnection(IDictionary<string, string> props)
    {
        return ConnectionFactory.GetInstance().CreateConnection(props);
    }
}