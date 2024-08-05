using System;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Markup.Xaml;
using GUI.ViewModels;
using GUI.Views;
using log4net;
using Network.NetworkUtils;

namespace GUI;

public class App : Application
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(App));
    private const int DefaultPort = 8888;
    private const string DefaultHost = "127.0.0.1";
    private LoginWindowViewModel? _loginWindowViewModel;

    public override void Initialize()
    {
        Log.Info("Initializing the app..");
        AvaloniaXamlLoader.Load(this);

        // Initializing the properties.
        Log.Info("Initializing the properties of the app...");
        var properties = ClientProperties.GetProperties();
        Log.Info("Properties initialized.");

        var port = DefaultPort;
        var hostname = DefaultHost;
        if (properties.ContainsKey("Port") == false)
        {
            Log.WarnFormat("Port couldn't be loaded. Using the default port: {0}", DefaultPort);
        }
        else
        {
            var result = Int32.TryParse(properties["Port"], out port);
            if (!result)
            {
                Log.WarnFormat("Port property not a number. Using the default port: {0}", DefaultPort);
                port = DefaultPort;
            }
        }

        if (properties.ContainsKey("Hostname") == false)
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

        // Initializing the proxy.
        Log.Info("Initializing the proxy...");
        var proxy = new ProtobufProxy(hostname!, port);

        // Initializing the view model.
        Log.Info("Initializing the view model...");
        _loginWindowViewModel = new LoginWindowViewModel
        {
            Proxy = proxy
        };
        Log.Info("View model initialized.");
        Log.Info("App initialized.");
    }

    public override void OnFrameworkInitializationCompleted()
    {
        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
            desktop.MainWindow = new LoginWindow
            {
                DataContext = _loginWindowViewModel
            };

        base.OnFrameworkInitializationCompleted();
    }
}