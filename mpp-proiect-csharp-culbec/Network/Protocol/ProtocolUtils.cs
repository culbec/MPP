using NetworkProtos.Protocol;

namespace Network.Protocol;

public static class ProtocolUtils
{
    /// <summary>
    /// Creates a request for the login operation.
    /// </summary>
    /// <param name="username">Username of the user that tries to login.</param>
    /// <param name="password">Password of the user that tries to login.</param>
    /// <returns>A Protobuf Request for the login operation.</returns>
    public static Request CreateLoginRequest(string username, string password)
    {
        var request = new Request
        {
            RequestType = Request.Types.type.Login,
            Username = username,
            Password = password
        };

        return request;
    }

    /// <summary>
    /// Creates a request for the logout operation.
    /// </summary>
    /// <param name="user">User that wants to logout.</param>
    /// <returns>A Protobuf Request for the logout operation.</returns>
    public static Request CreateLogoutRequest(Model.User user)
    {
        var request = new Request
        {
            RequestType = Request.Types.type.Logout,
            User = new User
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Username = user.Username
            }
        };

        return request;
    }

    /// <summary>
    /// Creates a request for the add participant operation.
    /// </summary>
    /// <param name="firstname">First name of the participant.</param>
    /// <param name="lastname">Last name of the participant.</param>
    /// <param name="team">Team of the participant.</param>
    /// <param name="engineCapacity">Engine capacity that the participant enrolled with.</param>
    /// <returns>A Protobuf Request for the add participant operation.</returns>
    public static Request CreateAddParticipantRequest(string firstname, string lastname, string team, int engineCapacity)
    {
        var request = new Request
        {
            RequestType = Request.Types.type.AddParticipant,
            Participant = new Participant
            {
                FirstName = firstname,
                LastName = lastname,
                Team = team,
                EngineCapacity = engineCapacity
            }
        };

        return request;
    }

    /// <summary>
    /// Creates a request for the find all participants of a given team operation.
    /// </summary>
    /// <param name="team">Team of the participants.</param>
    /// <returns>A Protobuf Request to find all participant of a given team operation.</returns>
    public static Request CreateFindParticipantsByTeamRequest(string team)
    {
        var request = new Request
        {
            RequestType = Request.Types.type.FindParticipantsByTeam,
            Team = team
        };

        return request;
    }

    /// <summary>
    /// Creates a request for the find all races operation.
    /// </summary>
    /// <returns>A Protobuf Request to find all races operation.</returns>
    public static Request CreateFindAllRacesRequest()
    {
        var request = new Request
        {
            RequestType = Request.Types.type.FindRaces
        };

        return request;
    }

    /// <summary>
    /// Creates a request for the find all engine capacities operation.
    /// </summary>
    /// <returns>A Protobuf Request to find all engine capacities operation.</returns>
    public static Request CreateFindAllRaceEngineCapacitiesRequest()
    {
        var request = new Request
        {
            RequestType = Request.Types.type.FindEngineCapacities
        };

        return request;
    }

    /// <summary>
    /// Creates a response for the login operation.
    /// </summary>
    /// <param name="user">User that logged in.</param>
    /// <returns>A Protobuf Response for the login operation.</returns>
    public static Response CreateLoginResponse(Model.User user)
    {
        var response = new Response
        {
            ResponseType = Response.Types.type.Ok,
            User = new User
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Username = user.Username
            }
        };

        return response;
    }

    /// <summary>
    /// Creates a response for the logout operation.
    /// </summary>
    /// <returns>A Protobuf Response for the logout operation.</returns>
    public static Response CreateLogoutResponse()
    {
        return new Response
        {
            ResponseType = Response.Types.type.Ok
        };
    }

    /// <summary>
    /// Creates a response for the add participant operation.
    /// </summary>
    /// <param name="participant">Participant that was added.</param>
    /// <returns>A Protobuf Response for the add participant operation.</returns>
    public static Response CreateAddParticipantResponse(Participant participant)
    {
        var response = new Response
        {
            ResponseType = Response.Types.type.Ok,
            Participant = participant
        };

        return response;
    }

    /// <summary>
    /// Creates a response for the participant added event for the observers.
    /// </summary>
    /// <param name="participant">Participant that was added.</param>
    /// <returns>A Protobuf Response for the participant added event</returns>
    public static Response CreateParticipantAddedResponse(Model.Participant participant)
    {
        var response = new Response
        {
            ResponseType = Response.Types.type.ParticipantAdded,
            Participant = new Participant
            {
                Id = new UUID
                {
                    Value = participant.Id.ToString()
                },
                FirstName = participant.FirstName,
                LastName = participant.LastName,
                Team = participant.Team,
                EngineCapacity = participant.EngineCapacity
            }
        };

        return response;
    }

    /// <summary>
    /// Creates a response for the find participants by a given team operation.
    /// </summary>
    /// <param name="participants">List of the found participants of the given team.</param>
    /// <returns>A Protobuf Response for the find participants by a given team operation.</returns>
    public static Response CreateFindParticipantsByTeamResponse(IEnumerable<Model.Participant> participants)
    {
        var participantProtos = participants.Select((participant, _) => new Participant
        {
            Id = new UUID
            {
                Value = participant.Id.ToString()
            },
            FirstName = participant.FirstName,
            LastName = participant.LastName,
            Team = participant.Team,
            EngineCapacity = participant.EngineCapacity
        }).ToList();

        var response = new Response
        {
            ResponseType = Response.Types.type.Ok,
            Participants = { participantProtos }
        };

        return response;
    }

    /// <summary>
    /// Creates a response for the find all races operation.
    /// </summary>
    /// <param name="races">Races that were found.</param>
    /// <returns>A Protobuf Response for the find all races operation.</returns>
    public static Response CreateFindAllRacesResponse(IEnumerable<Model.Race> races)
    {
        var raceProtos = races.Select((race, _) => new Race
        {
            Id = race.Id,
            EngineCapacity = race.EngineCapacity,
            NoParticipants = race.NoParticipants
        }).ToList();

        var response = new Response
        {
            ResponseType = Response.Types.type.Ok,
            Races = { raceProtos }
        };
        return response;
    }

    /// <summary>
    /// Creates a response for the find all engine capacities operation.
    /// </summary>
    /// <param name="engineCapacities">Engine capacities that were found.</param>
    /// <returns>A Protobuf Response for the find all engine capacities operation.</returns>
    public static Response CreateFindAllRaceEngineCapacitiesResponse(IEnumerable<int> engineCapacities)
    {
        var response = new Response
        {
            ResponseType = Response.Types.type.Ok,
            EngineCapacities = { engineCapacities }
        };

        return response;
    }

    public static readonly Response OkResponse = new()
    {
        ResponseType = Response.Types.type.Ok
    };

    /// <summary>
    /// Creates an error response.
    /// </summary>
    /// <param name="message">Message of the error.</param>
    /// <returns>The new error response.</returns>
    public static Response CreateErrorResponse(string message)
    {
        return new Response
        {
            ResponseType = Response.Types.type.Error,
            ErrorMessage = message
        };
    }

    /// <summary>
    /// Creates a closed connection response.
    /// </summary>
    /// <returns>Closed connection response.</returns>
    public static Response CreateConnectionClosedResponse()
    {
        return new Response
        {
            ResponseType = Response.Types.type.ConnectionClosed
        };
    }
}