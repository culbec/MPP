package Protocol;

import Model.Participant;
import Model.Race;
import Model.User;

import java.util.stream.StreamSupport;

public class ProtocolUtils {
    /**
     * Creates a request for logging in.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A request for logging in.
     */
    public static NetworkProtos.Request createLoginRequest(String username, String password) {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.LOGIN)
                .setUsername(username)
                .setPassword(password)
                .build();
    }

    /**
     * Creates a request for logging out.
     * @param user The user to log out.
     * @return A request for logging out.
     */
    public static NetworkProtos.Request createLogoutRequest(User user) {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.LOGOUT)
                .setUser(NetworkProtos.User.newBuilder()
                        .setId(user.getId())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setUsername(user.getUsername())
                        .build())
                .build();
    }

    /**
     * Creates a request for adding a participant.
     * @param firstName The first name of the participant.
     * @param lastName The last name of the participant.
     * @param team The team of the participant.
     * @param engineCapacity The engine capacity of the participant's motorcycle.
     * @return A request for adding a participant.
     */
    public static NetworkProtos.Request createAddParticipantRequest(String firstName, String lastName, String team, int engineCapacity) {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.ADD_PARTICIPANT)
                .setParticipant(NetworkProtos.Participant.newBuilder()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setTeam(team)
                        .setEngineCapacity(engineCapacity)
                        .build())
                .build();
    }

    /**
     * Creates a request for finding participants by team.
     * @param team The team to search for.
     * @return A request for finding participants by team.
     */
    public static NetworkProtos.Request createFindParticipantsByTeamRequest(String team) {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.FIND_PARTICIPANTS_BY_TEAM)
                .setTeam(team)
                .build();
    }

    /**
     * Creates a request to find all the saved races.
     * @return A request to find all the saved races.
     */
    public static NetworkProtos.Request createFindAllRacesRequest() {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.FIND_RACES)
                .build();
    }

    /**
     * Creates a request to find all the engine capacities of the saved races.
     * @return A request to find all the engine capacities of the saved races.
     */
    public static NetworkProtos.Request createFindAllRaceEngineCapacitiesRequest() {
        return NetworkProtos.Request.newBuilder()
                .setRequestType(NetworkProtos.Request.type.FIND_ENGINE_CAPACITIES)
                .build();
    }

    // OK response.
    public static NetworkProtos.Response OKResponse = NetworkProtos.Response.newBuilder()
            .setResponseType(NetworkProtos.Response.type.OK)
            .build();

    /**
     * Creates an error response.
     * @param message The error message.
     * @return An error response.
     */
    public static NetworkProtos.Response createErrorResponse(String message) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.ERROR)
                .setErrorMessage(message)
                .build();
    }

    /**
     * Creates a connection closed response.
     * @return The connection closed response.
     */
    public static NetworkProtos.Response createConnectionClosedResponse() {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.CONNECTION_CLOSED)
                .build();
    }

    /**
     * Creates a response for logging in.
     * @param user The user that logged in.
     * @return A response for logging in.
     */
    public static NetworkProtos.Response createLoginResponse(User user) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .setUser(NetworkProtos.User.newBuilder()
                        .setId(user.getId())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setUsername(user.getUsername())
                        .build())
                .build();
    }

    /**
     * Creates a response for logging out.
     * @return A response for logging out.
     */
    public static NetworkProtos.Response createLogoutResponse() {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .build();
    }

    /**
     * Creates a response for adding a participant.
     * @param participant The participant that was added.
     * @return A response for adding a participant.
     */
    public static NetworkProtos.Response createAddParticipantResponse(NetworkProtos.Participant participant) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .setParticipant(participant)
                .build();
    }

    /**
     * Creates a response for finding participants by team.
     * @param participant The participant that was found.
     * @return A response for finding participants by team.
     */
    public static NetworkProtos.Response createParticipantAddedResponse(Participant participant) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.PARTICIPANT_ADDED)
                .setParticipant(NetworkProtos.Participant.newBuilder()
                        .setFirstName(participant.getFirstName())
                        .setLastName(participant.getLastName())
                        .setTeam(participant.getTeam())
                        .setEngineCapacity(participant.getEngineCapacity())
                        .build())
                .build();
    }

    /**
     * Creates a response for finding participants by team.
     * @param participants The participants that were found.
     * @return A response for finding participants by team.
     */
    public static NetworkProtos.Response createFindParticipantsByTeamResponse(Iterable<Participant> participants) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .addAllParticipants(StreamSupport.stream(participants.spliterator(), false)
                        .map(participant -> NetworkProtos.Participant.newBuilder()
                                .setId(NetworkProtos.UUID.newBuilder()
                                        .setValue(participant.getId().toString()).build())
                                .setFirstName(participant.getFirstName())
                                .setLastName(participant.getLastName())
                                .setTeam(participant.getTeam())
                                .setEngineCapacity(participant.getEngineCapacity())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Creates a response for finding all saved races.
     * @param races Races saved.
     * @return A response for finding all saved races.
     */
    public static NetworkProtos.Response createFindAllRacesResponse(Iterable<Race> races) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .addAllRaces(StreamSupport.stream(races.spliterator(), false)
                        .map(race -> NetworkProtos.Race.newBuilder()
                                .setId(race.getId())
                                .setEngineCapacity(race.getEngineCapacity())
                                .setNoParticipants(race.getNoParticipants())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Creates a response for finding all engine capacities of the saved races.
     * @param engineCapacities Engine capacities of the saved races.
     * @return A response for finding all engine capacities of the saved races.
     */
    public static NetworkProtos.Response createFindAllRaceEngineCapacitiesResponse(Iterable<Integer> engineCapacities) {
        return NetworkProtos.Response.newBuilder()
                .setResponseType(NetworkProtos.Response.type.OK)
                .addAllEngineCapacities(engineCapacities)
                .build();
    }

}
