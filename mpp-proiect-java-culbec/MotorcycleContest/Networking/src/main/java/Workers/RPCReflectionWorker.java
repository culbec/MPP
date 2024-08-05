package Workers;

import CommonUtils.IService;
import CommonUtils.Observer;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.Participant;
import Model.User;
import NetworkUtils.ServerException;
import Protocol.NetworkProtos;
import Protocol.ProtocolUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;

public class RPCReflectionWorker implements Runnable, Observer {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final IService server;
    private final Socket connection;

    private InputStream inputStream;
    private OutputStream outputStream;

    private volatile boolean connected;

    public RPCReflectionWorker(IService server, Socket connection) throws ServerException {
        this.server = server;
        this.connection = connection;
        this.initializeConnection();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {
                NetworkProtos.Response response = ProtocolUtils.createConnectionClosedResponse();
                response.writeDelimitedTo(outputStream);
                outputStream.flush();

                connected = false;
                shutdown();
            } catch (IOException e) {
                logger.error("Error when closing the connection: {}", e.getMessage());
            }
        }));
    }

    /**
     * Initializes the connection.
     *
     * @throws ServerException If something went wrong.
     */
    private void initializeConnection() throws ServerException {
        try {
            this.outputStream = connection.getOutputStream();
            this.outputStream.flush();
            this.inputStream = connection.getInputStream();

            this.connected = true;

            // Flushing the output stream, so we have a clear channel.
            this.outputStream.flush();
        } catch (Exception e) {
            throw new ServerException("Error when creating the streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.traceEntry("Running the worker...");
        while (connected) {
            logger.info("Waiting for a request...");
            try {
                NetworkProtos.Request request = NetworkProtos.Request.parseDelimitedFrom(this.inputStream);

                if (request == null) {
                    logger.error("Error when reading the request: The request is null.");

                    logger.info("Checking if the connection is still up.");
                    if (this.connection.isClosed()) {
                        logger.error("The connection is closed.");
                        this.connected = false;
                        break;
                    }

                    logger.info("The connection is still up.");
                    logger.info("Notifying the client that the request was invalid.");
                    this.sendResponse(ProtocolUtils.createErrorResponse("Received request is null."));
                    continue;
                }

                logger.info("Received the request: {}", request.getRequestType().name());

                NetworkProtos.Response response = this.handleRequest(request);
                if (response != null) {
                    this.sendResponse(response);
                } else {
                    logger.error("Error when handling the request: The response is null.");
                }

            } catch (Exception e) {
                logger.error("Error when reading the request: {}", e.getMessage());
            }
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error("Error when sleeping the thread: {}", e.getMessage());
            }
        }

        logger.traceExit("Worker finished.");
        this.shutdown();
    }

    /**
     * Sends the response to the client.
     *
     * @param response The response to send.
     */
    private synchronized void sendResponse(NetworkProtos.Response response) {
        logger.traceEntry("Sending the response: {}", response.getResponseType().name());
        try {
            response.writeDelimitedTo(this.outputStream);
            this.outputStream.flush();
            logger.traceExit("Response sent.");
        } catch (IOException e) {
            logger.error("Error when sending the response: {}", e.getMessage());
        }
    }

    private static final NetworkProtos.Response OK_RESPONSE = NetworkProtos.Response.newBuilder()
            .setResponseType(NetworkProtos.Response.type.OK)
            .build();

    /**
     * Handles the request and returns the response.
     *
     * @param request The request to handle.
     * @return The response of the request.
     */
    private NetworkProtos.Response handleRequest(NetworkProtos.Request request) throws ServerException {
        logger.traceEntry("Handling the request: {}", request.getRequestType().name());
        String handlerName = "handle" + request.getRequestType().name();

        try {
            logger.info("Invoking the handler: {}", handlerName);
            Method method = this.getClass().getDeclaredMethod(handlerName, NetworkProtos.Request.class);

            NetworkProtos.Response response = (NetworkProtos.Response) method.invoke(this, request);
            logger.traceExit("Returning the response: {}", response);
            return response;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.error("Error when invoking the handler: {}", e.getMessage());
            throw new ServerException("Error when invoking the handler: " + e.getMessage());
        }
    }

    /**
     * Handles the LOGIN request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to log in the user.
     */
    private NetworkProtos.Response handleLOGIN(NetworkProtos.Request request) {
        logger.traceEntry("Handling the LOGIN request...");

        try {
            logger.info("Logging in the user...");
            User user = this.server.login(request.getUsername(), request.getPassword(), this);
            logger.info("User logged in: {}", user);

            logger.traceExit("Returning the response...");
            return ProtocolUtils.createLoginResponse(user);
        } catch (Exception e) {
            logger.error("Error when logging in: {}", e.getMessage());
            this.connected = false;
            return ProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles the LOGOUT request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to log out the user.
     */
    private NetworkProtos.Response handleLOGOUT(NetworkProtos.Request request) {
        logger.traceEntry("Handling the LOGOUT request...");

        try {
            NetworkProtos.User userProto = request.getUser();
            User user = new User.Builder()
                    .setId(userProto.getId())
                    .setFirstName(userProto.getFirstName())
                    .setLastName(userProto.getLastName())
                    .setUsername(userProto.getUsername())
                    .build();

            this.server.logout(user, this);
            this.connected = false;
            return ProtocolUtils.createLogoutResponse();
        } catch (ServiceException | AppException e) {
            logger.error("Error when logging out: {}", e.getMessage());
            return ProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles the ADD_PARTICIPANT request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to add the participant.
     */
    private NetworkProtos.Response handleADD_PARTICIPANT(NetworkProtos.Request request) {
        logger.traceEntry("Handling the ADD_PARTICIPANT request...");

        try {
            logger.info("Extracting the relevant data regarding the participant...");
            String firstName = request.getParticipant().getFirstName();
            String lastName = request.getParticipant().getLastName();
            String team = request.getParticipant().getTeam();
            int engineCapacity = request.getParticipant().getEngineCapacity();

            logger.info("Adding the participant...");
            this.server.addParticipant(firstName, lastName, team, engineCapacity);

            logger.traceExit("Returning the response...");
            return ProtocolUtils.createAddParticipantResponse(request.getParticipant());
        } catch (ServiceException | AppException e) {
            logger.error("Error when adding the participant: {}", e.getMessage());
            return ProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles the FIND_PARTICIPANTS_BY_TEAM request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to find the participants by team.
     */
    private NetworkProtos.Response handleFIND_PARTICIPANTS_BY_TEAM(NetworkProtos.Request request) {
        logger.traceEntry("Handling the FIND_PARTICIPANTS_BY_TEAM request...");

        try {
            logger.info("Finding the participants by team...");

            String team = request.getTeam();
            var participants = this.server.findParticipantsByTeam(team);
            logger.info("Participants found: {}", ((List<Participant>) participants).size());

            logger.traceExit("Returning the response...");
            return ProtocolUtils.createFindParticipantsByTeamResponse(participants);
        } catch (ServiceException e) {
            logger.error("Error when finding the participants by team: {}", e.getMessage());
            return ProtocolUtils.createErrorResponse(e.getMessage());
        } catch (AppException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the FIND_RACES request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to find all the races.
     */
    private NetworkProtos.Response handleFIND_RACES(NetworkProtos.Request request) {
        logger.traceEntry("Handling the FIND_RACES request...");

        try {
            logger.info("Finding all the races...");
            var races = this.server.findAllRaces();

            logger.traceExit("Returning the response...");
            return ProtocolUtils.createFindAllRacesResponse(races);
        } catch (ServiceException e) {
            logger.error("Error when trying to find all the races: {}", e.getMessage());
            return ProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles the FIND_ENGINE_CAPACITIES request.
     *
     * @param request The request to handle.
     * @return The response of the request after trying to find all the engine capacities.
     */
    private NetworkProtos.Response handleFIND_ENGINE_CAPACITIES(NetworkProtos.Request request) {
        logger.traceEntry("Handling the FIND_ENGINE_CAPACITIES request...");

        try {
            logger.info("Finding all the engine capacities of the races...");
            var engineCapacities = this.server.findAllRaceEngineCapacities();

            logger.traceExit("Returning the response...");
            return ProtocolUtils.createFindAllRaceEngineCapacitiesResponse(engineCapacities);
        } catch (ServiceException e) {
            logger.error("Error when trying to find all the engine capacities: {}", e.getMessage());
            return ProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    /**
     * Executes the procedures related to the shutdown process of the worker.
     */
    private void shutdown() {
        logger.traceEntry("Shutting down the worker...");
        try {
            logger.info("Closing the streams...");

            this.inputStream.close();
            this.outputStream.close();
            this.connection.close();
        } catch (IOException e) {
            logger.error("Error when closing the streams: {}", e.getMessage());
        }

        logger.traceExit("Client disconnected.");
    }

    @Override
    public void participantAdded(Participant participant) throws AppException {
        logger.traceEntry("Notifying the observer that a participant was added: {}", participant);
        NetworkProtos.Response response = ProtocolUtils.createParticipantAddedResponse(participant);

        try {
            this.sendResponse(response);
        } catch (Exception e) {
            logger.error("Error when notifying the observer: {}", e.getMessage());
            throw new AppException("Error when notifying the observer: " + e.getMessage());
        }
    }
}
