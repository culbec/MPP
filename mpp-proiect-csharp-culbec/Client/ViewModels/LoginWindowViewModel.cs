using System;
using System.Threading.Tasks;
using Common.Exceptions;
using GUI.Views;
using log4net;
using MsBox.Avalonia;
using Network.NetworkUtils;
using ReactiveUI;

namespace GUI.ViewModels;

public class LoginWindowViewModel : ViewModelBase
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(LoginWindowViewModel));
    public ProtobufProxy? Proxy { init; get; }

    public event Action? HideRequested;
    public Action? ShowRequested;

    private string? _username;

    public string? Username
    {
        get => _username;
        set => this.RaiseAndSetIfChanged(ref _username, value);
    }

    private string? _password;

    public string? Password
    {
        get => _password;
        set => this.RaiseAndSetIfChanged(ref _password, value);
    }

    private string? _errorMessage;

    public string? ErrorMessage
    {
        get => _errorMessage;
        set => this.RaiseAndSetIfChanged(ref _errorMessage, value);
    }

    public async Task LoginAction()
    {
        Log.Info("Verifying if the passed credentials are not empty or null...");
        if (string.IsNullOrWhiteSpace(_username) || string.IsNullOrWhiteSpace(_password))
        {
            Log.Error("The username or password is empty or null.");
            ErrorMessage = "Username and password must not be empty!";
            return;
        }

        Log.Info("The passed credentials are not empty or null.");
        ErrorMessage = "";

        try
        {
            Log.Info("Trying to login...");
            var mainWindowViewModel = new MainWindowViewModel(Proxy!);
            var user = Proxy!.Login(_username, _password, mainWindowViewModel);

            Log.Info("Login successful.");
            mainWindowViewModel.User = user;
            mainWindowViewModel.LoginWindow = this;

            Log.Info("Creating the main window...");
            var mainWindow = new MainWindow
            {
                DataContext = mainWindowViewModel
            };
            Log.Info("Main window created.");
            mainWindowViewModel.Initialize();

            Log.Info("Showing the main window...");
            mainWindow.Show();

            Log.Info("Closing the login window...");
            HideRequested?.Invoke();
        }
        catch (ServiceException e)
        {
            Log.ErrorFormat("Something went wrong while trying to login: {0}", e.Message);
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", e.Message);
            await messageBox.ShowAsync();
        }
    }
}