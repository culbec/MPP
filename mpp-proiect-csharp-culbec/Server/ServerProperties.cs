using System.Collections.ObjectModel;
using System.Configuration;
using log4net.Config;

namespace Server;

public static class ServerProperties
{
    public static IDictionary<string, string> GetProperties()
    {
        var fileMap = new ExeConfigurationFileMap { ExeConfigFilename = "server.config" };
        var configuration = ConfigurationManager.OpenMappedExeConfiguration(fileMap, ConfigurationUserLevel.None);

        XmlConfigurator.Configure(new FileInfo("server.config"));
        var props = new SortedList<string, string>();

        var connectionString = GetConnectionStringByName(configuration, "MariaDBConnectionString");
        if (connectionString != null) props.Add("ConnectionString", connectionString);

        var appSettings = GetAppSettings(configuration);
        if (appSettings != null)
        {
            if (appSettings.ContainsKey("port") && appSettings.ContainsKey("hostname"))
            {
                props.Add("Port", appSettings["port"]);
                props.Add("Hostname", appSettings["hostname"]);
            }
        }

        return props;
    }

    private static IDictionary<string, string>? GetAppSettings(Configuration configuration)
    {
        var appSettings = configuration.AppSettings;
        var port = appSettings.Settings["port"].Value;
        var hostname = appSettings.Settings["hostname"].Value;

        if (port == null || hostname == null) return null;
        var settings = new Dictionary<string, string> { { "hostname", hostname }, { "port", port } };
        return settings;
    }

    private static string? GetConnectionStringByName(Configuration configuration, string name)
    {
        var connectionStringSettings = configuration.ConnectionStrings.ConnectionStrings[name];

        return connectionStringSettings?.ConnectionString;
    }
}