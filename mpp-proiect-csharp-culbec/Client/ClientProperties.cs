using System.Collections.Generic;
using System.Configuration;
using System.IO;
using log4net.Config;

namespace GUI;

public static class ClientProperties
{
    public static IDictionary<string, string> GetProperties()
    {
        var fileMap = new ExeConfigurationFileMap { ExeConfigFilename = "client.config" };
        var configuration = ConfigurationManager.OpenMappedExeConfiguration(fileMap, ConfigurationUserLevel.None);

        XmlConfigurator.Configure(new FileInfo("client.config"));
        var props = new SortedList<string, string>();

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
}