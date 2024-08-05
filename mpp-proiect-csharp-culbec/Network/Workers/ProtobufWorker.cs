using System.Diagnostics;
using System.Net.Sockets;
using System.Reflection;
using Common.CommonUtils;
using Common.Exceptions;
using Google.Protobuf;
using log4net;
using Network.Protocol;
using NetworkProtos.Protocol;
using Participant = Model.Participant;

namespace Network.Workers;

public class ProtobufWorker : IObserver
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ProtobufWorker));
    private readonly IService _server;
    private readonly TcpClient _client;

    private NetworkStream _stream;
    private volatile bool _connected;

    public ProtobufWorker(IService server, TcpClient client)
    {
        _server = server;
        _client = client;
        InitializeConnection();
    }

    /// <summary>
    /// Runs the worker. Waits for requests from the client and send the associated response back.
    /// </summary>
    public void Run()
    {
        Log.Info("Running the worker...");

        while (_connected)
        {
            Log.Info("Waiting for requests...");
            try
            {
                var request = Request.Parser.ParseDelimitedFrom(_stream);
                if (request == null)
                {
                    Log.Error("The request is null!");

                    Log.Info("Checking if the connection is still up.");
                    if (_client.Client.Connected == false)
                    {
                        Log.Error("The connection is down. Shutting down the worker...");
                        _connected = false;
                        break;
                    }

                    Log.Info("The connection is still up.");
                    Log.Info("Notifying the client that the request was invalid.");
                    var errorResponse = ProtocolUtils.CreateErrorResponse("The request is null!");
                    SendResponse(errorResponse);
                    continue;
                }

                Log.InfoFormat("Received request: {0}", request);
                Log.Info("Handling the request...");
                var response = HandleRequest(request);
                if (response != null)
                {
                    SendResponse(response);
                }

                else
                {
                    Log.Error("The request is null!");
                    var errorResponse = ProtocolUtils.CreateErrorResponse("The request is null!");
                    SendResponse(errorResponse);
                }
            }
            catch (Exception e)
            {
                Log.ErrorFormat("An error occurred: {0}", e.Message);
            }

            try
            {
                Thread.Sleep(2000);
            }
            catch (Exception e)
            {
                Log.ErrorFormat("An error occurred: {0}", e.Message);
            }
        }

        Shutdown();
    }

    private void InitializeConnection()
    {
        try
        {
            Log.Info("Requesting the stream from the client and connecting the worker...");
            _stream = _client.GetStream();
            _stream.Flush();
            _connected = true;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException(e.Message);
        }
    }

    /// <summary>
    /// Shuts down the worker.
    /// </summary>
    private void Shutdown()
    {
        try
        {
            Log.Info("Closing the connection...");
            _stream.Close();
            _client.Close();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
        }
    }

    private Response? HandleRequest(Request request)
    {
        Log.InfoFormat("Handling the request: {0}", request);
        var handlerName = "Handle" + request.RequestType;

        try
        {
            Log.InfoFormat("Invoking the handler: {0}", handlerName);
            var methodInfo = GetType().GetMethod(handlerName, BindingFlags.NonPublic | BindingFlags.Instance);
            if (methodInfo == null)
            {
                Log.ErrorFormat("The handler {0} does not exist!", handlerName);
                return null;
            }

            var response = methodInfo.Invoke(this, [request]);
            Log.InfoFormat("Handler invoked. Response: {0}", response);

            return (Response)response!;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException("An error occurred: " + e.Message);
        }
    }

    /// <summary>
    /// Sends the response back to the client.
    /// </summary>
    /// <param name="response">Response that is being sent.</param>
    private void SendResponse(IMessage response)
    {
        Log.InfoFormat("Sending response: {0}", response);
        try
        {
            response.WriteDelimitedTo(_stream);
            _stream.Flush();
            Log.Info("Response sent...");
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
        }
    }

    /// <summary>
    /// Handler for the login operation.
    /// </summary>
    /// <param name="request">Request for the login operation.</param>
    /// <returns>The response for the login operation.</returns>
    private Response HandleLogin(Request request)
    {
        Log.Info("Handling the Login Request...");

        try
        {
            Log.Info("Logging in the user...");
            var user = _server.Login(request.Username, request.Password, this);

            Log.InfoFormat("User logged in: {0}", user);

            var response = ProtocolUtils.CreateLoginResponse(user);
            Log.InfoFormat("Login response created: {0}", response);
            return response;
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error when logging in the user: {0}", e.Message);
            _connected = false;
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    /// <summary>
    /// Handler for the logout operation.
    /// </summary>
    /// <param name="request">Request of the logout operation.</param>
    /// <returns>The response to the logout operation.</returns>
    private Response HandleLogout(Request request)
    {
        Log.Info("Handling the Logout Request...");

        try
        {
            var userProto = request.User;
            var user = Model.User.NewBuilder()
                .SetId(userProto.Id)
                .SetFirstName(userProto.FirstName)
                .SetLastName(userProto.LastName)
                .SetUsername(userProto.Username)
                .Build();

            _server.Logout(user, this);
            _connected = false;

            return ProtocolUtils.CreateLogoutResponse();
        }
        catch (Exception e)
        {
            Log.ErrorFormat("Error when logging out the user: {0}", e.Message);
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    /// <summary>
    /// Handler for the add participant operation.
    /// </summary>
    /// <param name="request">Request of the add participant operation.</param>
    /// <returns></returns>
    private Response HandleAddParticipant(Request request)
    {
        Log.Info("Handling the ADD_PARTICIPANT request...");

        try
        {
            Log.Info("Extracting the participant from the request...");
            var participant = Participant.NewBuilder()
                .SetId(Guid.NewGuid())
                .SetFirstName(request.Participant.FirstName)
                .SetLastName(request.Participant.LastName)
                .SetTeam(request.Participant.Team)
                .SetEngineCapacity(request.Participant.EngineCapacity)
                .Build();

            Log.InfoFormat("Extracted the participant: {0}", participant);
            Log.Info("Adding the participant...");

            _server.AddParticipant(participant.FirstName!, participant.LastName!, participant.Team!,
                participant.EngineCapacity);
            Log.Info("Participant added successfully! Returning the response...");

            return ProtocolUtils.CreateAddParticipantResponse(request.Participant);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    /// <summary>
    /// Handler for the find participants by team operation.
    /// </summary>
    /// <param name="request">Request of the operation.</param>
    /// <returns>A response signaling the success of the operation or not.</returns>
    private Response HandleFindParticipantsByTeam(Request request)
    {
        Log.Info("Handling the FIND_PARTICIPANTS_BY_TEAM request...");

        try
        {
            Log.Info("Retrieving the team...");
            var team = request.Team;
            var participants = _server.FindParticipantsByTeam(team!).ToList();
            Log.InfoFormat("Found {0} participants. Returning the response...", participants.Count);

            return ProtocolUtils.CreateFindParticipantsByTeamResponse(participants);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    /// <summary>
    /// Handler for the find races operation.
    /// </summary>
    /// <param name="request">Request of the operation.</param>
    /// <returns>A response signaling the success of the operation or not.</returns>
    private Response HandleFindRaces(Request request)
    {
        Log.Info("Handling the FIND_RACES request...");

        try
        {
            Log.Info("Finding all the races...");
            var races = _server.FindAllRaces().ToList();
            Log.InfoFormat("Found {0} races. Returning the response...", races.Count);

            return ProtocolUtils.CreateFindAllRacesResponse(races);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    /// <summary>
    /// Handler for the find engine capacities operation.
    /// </summary>
    /// <param name="request">Request of the operation.</param>
    /// <returns>A response signaling the success of the operation or not.</returns>
    private Response HandleFindEngineCapacities(Request request)
    {
        Log.Info("Handling the FIND_ENGINE_CAPACITIES request...");

        try
        {
            Log.Info("Finding all the engine capacities...");
            var engineCapacities = _server.FindAllRaceEngineCapacities().ToList();
            Log.InfoFormat("Found {0} races. Returning the response...", engineCapacities.Count);

            return ProtocolUtils.CreateFindAllRaceEngineCapacitiesResponse(engineCapacities);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            return ProtocolUtils.CreateErrorResponse(e.Message);
        }
    }

    public void ParticipantAdded(Participant participant)
    {
        Log.InfoFormat("Notifying the observer that this new participant was added: {0}", participant);
        var response = ProtocolUtils.CreateParticipantAddedResponse(participant);

        try
        {
            Log.Info("Sending the response...");
            SendResponse(response);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred when notifying the observer: {0}", e.Message);
            throw new AppException("An error occurred when notifying the observer: " + e.Message);
        }
    }
}