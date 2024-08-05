// See https://aka.ms/new-console-template for more information

using log4net;
using Network.NetworkUtils;
using Persistence.Repository;
using Server.Service;

namespace Server;

internal static class StartServer
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(StartServer));
    private const int DefaultPort = 8888;
    private const string DefaultHost = "127.0.0.1";

    public static void Main(string[] args)
    {
        Log.Info("Starting the server...");

        Log.Info("Loading the server configuration...");
        var properties = ServerProperties.GetProperties();

        if (!properties.ContainsKey("ConnectionString"))
        {
            Log.Error("Couldn't load the connection string!");
            return;
        }

        var port = DefaultPort;
        var hostname = DefaultHost;
        if (!properties.TryGetValue("Port", out var property))
        {
            Log.WarnFormat("Port couldn't be loaded. Using the default port: {0}", DefaultPort);
        }
        else
        {
            var result = int.TryParse(property, out port);
            if (!result)
            {
                Log.WarnFormat("Port property not a number. Using the default port: {0}", DefaultPort);
                port = DefaultPort;
            }
        }

        if (!properties.ContainsKey("Hostname"))
        {
            Log.WarnFormat("Hostname couldn't be loaded. Using the default hostname: {0}", DefaultHost);
        }
        else
        {
            var result = properties.TryGetValue("Hostname", out hostname);
            if (!result)
            {
                Log.WarnFormat("Hostname couldn't be loaded. Using the default hostname: {0}", DefaultHost);
                hostname = DefaultHost;
            }
        }

        Log.Info("Initializing the repositories...");
        var repositoryParticipant = new RepositoryParticipantDb(properties);
        //var repositoryUser = new RepositoryUserDb(properties);
        var repositoryUser = new RepositoryUserEfCore(properties);
        var repositoryRace = new RepositoryRaceDb(properties);

        Log.Info("Initializing the service...");
        var serviceParticipant = new ServiceParticipantImpl(repositoryParticipant);
        var serviceUser = new ServiceUserImpl(repositoryUser);
        var serviceRace = new ServiceRaceImpl(repositoryRace);
        var service = new ServiceImpl(serviceParticipant, serviceUser, serviceRace);

        Log.InfoFormat("Starting the server...");
        var server = new ProtobufServer(hostname!, port, service);
        try
        {
            server.Start();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error when starting the server: {0}", e.Message);
        }
    }
}