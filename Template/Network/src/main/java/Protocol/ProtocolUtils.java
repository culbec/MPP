package Protocol;

import Model.User;

public class ProtocolUtils {
    // REQUESTS

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

    // RESPONSES

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
}
