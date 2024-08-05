using System.Collections.Concurrent;
using System.Net.Sockets;
using System.Reflection;
using Common.CommonUtils;
using Common.Exceptions;
using Google.Protobuf;
using log4net;
using Network.Protocol;
using NetworkProtos.Protocol;
using Participant = Model.Participant;
using Race = Model.Race;
using User = Model.User;

namespace Network.NetworkUtils;

public class ProtobufProxy : IService
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ProtobufProxy));

    private readonly string _hostname;
    private readonly int _port;
    private IGuiObserver? _client;

    private NetworkStream? _stream;
    private TcpClient? _connection;

    private readonly BlockingCollection<Response> _responses = new();
    private volatile bool _finished;

    public ProtobufProxy(string hostname, int port)
    {
        _hostname = hostname;
        _port = port;
    }

    private void SetClient(IObserver client)
    {
        _client = (IGuiObserver) client;
    }

    private void InitializeConnection()
    {
        Log.InfoFormat("Initializing the connection with: {0}:{1}", _hostname, _port);
        try
        {
            _connection = new TcpClient(_hostname, _port);
            _stream = _connection.GetStream();
            _finished = false;

            Log.Info("Connection established. Starting the reader...");
            StartReader();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred when initializing the connection: {0}", e.Message);
            throw new AppException("An error occurred when initializing the connection: " + e.Message);
        }
    }

    private void CloseConnection()
    {
        Log.Info("Closing the connection...");
        try
        {
            _connection?.Close();
            _stream?.Close();
            Log.Info("Connection closed!");
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred when closing the connection: {0}", e.Message);
        }
    }

    private void SendRequest(IMessage request)
    {
        if (_stream is not { CanWrite: true })
        {
            Log.Error("Cannot send a request. The stream is not available.");
            throw new AppException("The request cannot be processed. Server communication is not available.");
        }

        Log.InfoFormat("Sending the request: {0}", request);

        try
        {
            request.WriteDelimitedTo(_stream);
            _stream.Flush();
            Log.Info("Request sent...");
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred when trying to process the request: {0}", e.Message);
            throw new AppException("The request couldn't be processed: " + e.Message);
        }
    }

    private Response ReadResponse()
    {
        Log.Info("Reading a response...");
        try
        {
            var response = _responses.Take();
            Log.Info("Response read successfully!");
            return response;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException("Error when reading the response: " + e.Message);
        }
    }

    /// <summary>
    /// Verifies if a response is an unrequested update.
    /// </summary>
    /// <param name="response">Response received.</param>
    /// <returns>True if the response is an update, false otherwise.</returns>
    private static bool IsUpdate(Response response)
    {
        return response.ResponseType == Response.Types.type.ParticipantAdded;
    }

    /// <summary>
    /// Checks if the server is closed.
    /// </summary>
    /// <param name="response">Response received.</param>
    /// <returns>True if the server is closed, false otherwise.</returns>
    private static bool IsServerClosed(Response response)
    {
        return response.ResponseType == Response.Types.type.ConnectionClosed;
    }

    private void HandleUpdate(Response response)
    {
        Log.InfoFormat("Handling the update response: {0}", response);
        var handlerName = "Handle" + response.ResponseType;

        try
        {
            Log.InfoFormat("Invoking the handler: {0}", handlerName);
            var methodInfo = GetType().GetMethod(handlerName,  BindingFlags.NonPublic | BindingFlags.Instance);

            if (methodInfo == null)
            {
                Log.ErrorFormat("The method {0} does not exist!", handlerName);
                return;
            }

            methodInfo.Invoke(this, [response]);
            Log.Info("Handler invoked!");
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException("An error occurred: " + e.Message);
        }

        Log.Info("Update response handled successfully!");
    }

    private void HandleParticipantAdded(Response response)
    {
        Log.InfoFormat("Handling the PARTICIPANT_ADDED response...");
        var participant = Participant.NewBuilder()
            .SetId(Guid.NewGuid())
            .SetFirstName(response.Participant.FirstName)
            .SetLastName(response.Participant.LastName)
            .SetTeam(response.Participant.Team)
            .SetEngineCapacity(response.Participant.EngineCapacity)
            .Build();

        try
        {
            Log.InfoFormat("Notifying the client that this participant was added: {0}", participant);
            _client!.ParticipantAdded(participant);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException("An error occurred: " + e.Message);
        }
    }

    private void StartReader()
    {
        Log.Info("Starting the reader thread...");
        try
        {
            var thread = new Thread(Run);
            thread.Start();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Couldn't start the reader: {0}", e.Message);
            throw new AppException("An error occurred when trying to create the reader thread: " + e.Message);
        }
    }

    private void Run()
    {
        while (!_finished)
        {
            try
            {
                Log.Info("Waiting for a response...");
                var response = Response.Parser.ParseDelimitedFrom(_stream);

                Log.InfoFormat("Received a response: {0}", response);
                if (response != null)
                {
                    if (IsServerClosed(response))
                    {
                        Log.Warn("The server has been closed. The app will be closed.");
                        try
                        {
                            CloseConnection();
                            _client?.ShutdownGui();
                            _finished = true;
                            break;
                        }
                        catch (Exception e)
                        {
                            Log.ErrorFormat("An error occurred: {0}", e.Message);
                        }
                    }

                    if (IsUpdate(response))
                    {
                        Log.Info("Handling unrequested update request.");
                        HandleUpdate(response);
                    }
                    else
                    {
                        Log.Info("Saving a requested response.");
                        _responses.Add(response);
                    }
                }
                else
                {
                    Log.Error("Couldn't receive the response from the server!");
                    var errorResponse =
                        ProtocolUtils.CreateErrorResponse("Couldn't receive the response from the server!");
                    _responses.Add(errorResponse);
                }
            }
            catch (Exception e)
            {
                Log.ErrorFormat("An error occurred when reading a response: {0}", e.Message);
            }
        }

        CloseConnection();
    }

    public IEnumerable<Participant> FindParticipantsByTeam(string team)
    {
        Log.InfoFormat("Finding participant of team: {0}", team);

        try
        {
            Log.Info("Sending the request...");
            var request = ProtocolUtils.CreateFindParticipantsByTeamRequest(team);
            SendRequest(request);

            Log.Info("Reading the response...");
            var response = ReadResponse();

            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.Info("Participants found successfully!");
                var participants = response.Participants.Select(participant => Participant.NewBuilder()
                    .SetId(Guid.NewGuid())
                    .SetFirstName(participant.FirstName)
                    .SetLastName(participant.LastName)
                    .SetTeam(participant.Team)
                    .SetEngineCapacity(participant.EngineCapacity)
                    .Build()).ToList();
                return participants;
            }

            Log.ErrorFormat("Error finding participants by team: {0}", response.ErrorMessage);
            throw new ServiceException("Error finding participants by team: " + response.ErrorMessage);
        }
        catch (AppException e)
        {
            Log.ErrorFormat("Error finding participants by team: {0}", e.Message);
            throw new ServiceException("Error finding participants by team: " + e.Message);
        }
    }

    public void AddParticipant(string firstName, string lastName, string team, int engineCapacity)
    {
        Log.InfoFormat("Adding the participant with fields: {0}, {1}, {2}, {3}", firstName, lastName, team,
            engineCapacity);

        try
        {
            Log.Info("Sending the request...");
            var request = ProtocolUtils.CreateAddParticipantRequest(firstName, lastName, team, engineCapacity);
            SendRequest(request);

            Log.Info("Reading the response...");
            var response = ReadResponse();

            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.Info("Participant added successfully!");
                return;
            }

            Log.ErrorFormat("Error adding the participant: {0}", response.ErrorMessage);
            throw new ServiceException("Error adding the participant: " + response.ErrorMessage);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error adding the participant: {0}", e.Message);
            throw new ServiceException("Error adding the participant: " + e.Message);
        }
    }

    public IEnumerable<Race> FindAllRaces()
    {
        Log.Info("Finding all the races...");

        try
        {
            Log.Info("Sending the request...");
            var request = ProtocolUtils.CreateFindAllRacesRequest();
            SendRequest(request);

            Log.Info("Reading the response...");
            var response = ReadResponse();

            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.InfoFormat("Found {0} races", response.Races.Count);
                return response.Races.Select(race => Race.NewBuilder()
                    .SetId(race.Id)
                    .SetEngineCapacity(race.EngineCapacity)
                    .SetNoParticipants(race.NoParticipants)
                    .Build()).ToList();
            }

            Log.ErrorFormat("Error finding all races: {0}", response.ErrorMessage);
            throw new ServiceException("Error finding all races: " + response.ErrorMessage);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error finding all races: {0}", e.Message);
            throw new ServiceException("Error finding all races: " + e.Message);
        }
    }

    public IEnumerable<int> FindAllRaceEngineCapacities()
    {
        Log.Info("Finding race engine capacities...");

        try
        {
            Log.Info("Sending the request...");
            var request = ProtocolUtils.CreateFindAllRaceEngineCapacitiesRequest();
            SendRequest(request);

            Log.Info("Reading the response...");
            var response = ReadResponse();

            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.InfoFormat("Found {0} engine capacities", response.EngineCapacities.Count);
                return response.EngineCapacities.ToList();
            }

            Log.ErrorFormat("Error finding all engine capacities: {0}", response.ErrorMessage);
            throw new ServiceException("Error finding all engine capacities: " + response.ErrorMessage);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error finding all engine capacities: {0}", e.Message);
            throw new ServiceException("Error finding all engine capacities: " + e.Message);
        }
    }

    public User Login(string username, string password, IObserver client)
    {
        Log.InfoFormat("Logging in with username: {0}", username);
        try
        {
            Log.Info("Initializing the connection...");
            InitializeConnection();

            Log.Info("Connection initialized! Sending the login request...");
            var request = ProtocolUtils.CreateLoginRequest(username, password);
            SendRequest(request);

            var response = ReadResponse();
            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.Info("Logged in successfully!");
                SetClient(client);
                return User.NewBuilder()
                    .SetId(response.User.Id)
                    .SetFirstName(response.User.FirstName)
                    .SetLastName(response.User.LastName)
                    .SetUsername(username)
                    .Build();
            }

            Log.ErrorFormat("Error logging in: {0}", response.ErrorMessage);
            _finished = true;
            throw new ServiceException("Error logging in: " + response.ErrorMessage);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            _finished = true;
            throw new ServiceException("An error occurred: " + e.Message);
        }
    }

    public void Logout(User user, IObserver client)
    {
        Log.InfoFormat("Logging out the user: {0}", user);

        try
        {
            Log.Info("Sending the logout request...");
            var request = ProtocolUtils.CreateLogoutRequest(user);
            SendRequest(request);

            Log.Info("Reading the response...");
            var response = ReadResponse();

            if (response.ResponseType == Response.Types.type.Ok)
            {
                Log.Info("Logged out successfully!");
                _client = null;
                _finished = true;
                return;
            }

            Log.ErrorFormat("Error logging out: {0}", response.ErrorMessage);
            throw new ServiceException("Error logging out: " + response.ErrorMessage);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new ServiceException("An error occurred: " + e.Message);
        }
    }
}