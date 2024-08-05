using System;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Threading;
using Avalonia.Threading;
using Common.CommonUtils;
using Common.Exceptions;
using log4net;
using Model;
using MsBox.Avalonia;
using MsBox.Avalonia.Enums;
using Network.NetworkUtils;
using ReactiveUI;
using ReactiveUI.Fody.Helpers;

namespace GUI.ViewModels;

public class MainWindowViewModel : ViewModelBase, IGuiObserver
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(MainWindowViewModel));

    [Reactive] public ObservableCollection<Race>? Races { get; private set; }
    [Reactive] public ObservableCollection<Participant>? ParticipantsOfTeam { get; private set; }

    public LoginWindowViewModel LoginWindow { get; set; }
    private bool IsLoggedOut { get; set; }
    public event Action? LogoutRequested;
    public event Action? ClosedConnection;

    private ProtobufProxy? Proxy { get; }

    public string WelcomeMessage => "Welcome, " + User!.FirstName + " " + User!.LastName + "!";

    public User? User { get; set; }

    private string? _firstName;

    public string? FirstName
    {
        get => _firstName;
        set => this.RaiseAndSetIfChanged(ref _firstName, value);
    }

    private string? _lastName;

    public string? LastName
    {
        get => _lastName;
        set => this.RaiseAndSetIfChanged(ref _lastName, value);
    }

    private string? _team;

    public string? Team
    {
        get => _team;
        set => this.RaiseAndSetIfChanged(ref _team, value);
    }

    private ObservableCollection<int>? _engineCapacities;

    public ObservableCollection<int>? EngineCapacities
    {
        get => _engineCapacities;
        set => this.RaiseAndSetIfChanged(ref _engineCapacities, value);
    }

    private int _selectedEngineCapacity;

    public int SelectedEngineCapacity
    {
        get => _selectedEngineCapacity;
        set => this.RaiseAndSetIfChanged(ref _selectedEngineCapacity, value);
    }

    private string? _teamSearch;

    public string? TeamSearch
    {
        get => _teamSearch;
        set => this.RaiseAndSetIfChanged(ref _teamSearch, value);
    }

    internal void Initialize()
    {
        Log.Info("Initializing the MainWindowViewModel.");
        try
        {
            InitRaceTable();
            InitEngineCapacities();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new IOException("An error occurred when initializing the MainWindowViewModel: " + e.Message);
        }
    }

    private async void InitRaceTable()
    {
        Log.Info("Initializing the race table.");
        try
        {
            var races = Proxy!.FindAllRaces().ToList();
            Log.InfoFormat("Found {0} races", races.Count);
            Races = new ObservableCollection<Race>(races);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", e.Message);
            await messageBox.ShowAsync();
        }

        Log.Info("Race table initialized.");
    }

    private async void InitEngineCapacities()
    {
        Log.Info("Initializing the engine capacities combo box.");
        try
        {
            var engineCapacities = Proxy!.FindAllRaceEngineCapacities().ToList();
            Log.InfoFormat("Found {0} engine capacities", engineCapacities.Count);
            EngineCapacities = new ObservableCollection<int>(engineCapacities);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", e.Message);
            await messageBox.ShowAsync();
        }

        Log.Info("Engine capacities combo box initialized.");
    }

    public MainWindowViewModel()
    {
    }

    public MainWindowViewModel(ProtobufProxy proxy)
    {
        Proxy = proxy;
    }

    public void LogoutAction()
    {
        if (IsLoggedOut) return;
        Log.InfoFormat("Logging out the user {0}", User);

        try
        {
            Proxy!.Logout(User!, this);
            Log.Info("User logged out successfully.");
            IsLoggedOut = true;
            LogoutRequested!.Invoke();
            Environment.Exit(0);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
        }
    }

    public async void AddAction()
    {
        var errors = "";

        if (string.IsNullOrWhiteSpace(FirstName))
        {
            errors += "First name must not be empty!\n";
        }

        if (string.IsNullOrWhiteSpace(LastName))
        {
            errors += "Last name must not be empty!\n";
        }

        if (string.IsNullOrWhiteSpace(Team))
        {
            errors += "Team must not be empty!\n";
        }

        if (errors.Length > 0)
        {
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", errors);
            await messageBox.ShowAsync();
            return;
        }

        try
        {
            Proxy!.AddParticipant(FirstName!, LastName!, Team!,
                EngineCapacities![_selectedEngineCapacity]);
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Success", "Participant added successfully!");
            await messageBox.ShowAsync();
        }
        catch (ServiceException e)
        {
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", e.Message);
            await messageBox.ShowAsync();
        }
    }

    public async void ShowParticipantsAction()
    {
        if (string.IsNullOrWhiteSpace(TeamSearch))
        {
            var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", "Team must not be empty!");
            await messageBox.ShowAsync();
            return;
        }

        Dispatcher.UIThread.Post(async () =>
        {
            try
            {
                var participants = Proxy!.FindParticipantsByTeam(TeamSearch!).ToList();
                ParticipantsOfTeam = new ObservableCollection<Participant>(participants);
            }
            catch (ServiceException e)
            {
                var messageBox = MessageBoxManager.GetMessageBoxStandard("Error", e.Message);
                await messageBox.ShowAsync();
            }
        });
    }

    public void ParticipantAdded(Participant participant)
    {
        Dispatcher.UIThread.Post(() =>
        {
            // Updating the race table.
            var engineCapacity = participant.EngineCapacity;
            var oldRace = Races?.FirstOrDefault(race => race.EngineCapacity == engineCapacity);

            if (oldRace != null)
            {
                var index = Races?.IndexOf(oldRace);
                if (index != null)
                {
                    Races![index.Value] = Race.NewBuilder()
                        .SetId(oldRace.Id)
                        .SetEngineCapacity(oldRace.EngineCapacity)
                        .SetNoParticipants(oldRace.NoParticipants + 1)
                        .Build();
                }
            }

            // Updating the participants team table if the team is selected.
            var team = participant.Team;
            if (TeamSearch == team)
            {
                ParticipantsOfTeam?.Add(participant);
            }
        });
    }

    public void ShutdownGui()
    {
        Dispatcher.UIThread.Post(async () =>
        {
            Log.Info("The server has been closed. Shutting down the GUI...");
            try
            {
                var messageBox = MessageBoxManager.GetMessageBoxStandard("Server closed",
                    "The server has been closed. Shutting down...", ButtonEnum.Ok);
                await messageBox.ShowAsync();

                // Notifying that the client is now logged out, not because of his part.
                IsLoggedOut = true;

                Log.Info("Showing the login window.");
                LoginWindow.ShowRequested?.Invoke();

                Log.Info("Closing the window.");
                ClosedConnection?.Invoke();
            }
            catch (Exception ex)
            {
                Log.ErrorFormat("An error occurred: {0}", ex.Message);
            }
        });
    }
}