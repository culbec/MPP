package NetworkUtils;

import CommonUtils.GUIObserver;
import CommonUtils.IService;
import CommonUtils.Observer;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.Participant;
import Model.Race;
import Model.User;
import Protocol.NetworkProtos;
import Protocol.ProtocolUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtobufProxy implements IService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final String hostname;
    private final int port;
    private GUIObserver client;

    private Socket connection;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final BlockingQueue<NetworkProtos.Response> responses = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public ProtobufProxy(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void setClient(Observer client) {
        this.client = (GUIObserver) client;
    }

    /**
     * Initializes the connection with the server.
     *
     * @throws AppException if an error occurs while initializing the connection.
     */
    private void initializeConnection() throws AppException {
        logger.traceEntry("Initializing the connection...");

        try {
            logger.info("Initializing the socket...");
            this.connection = new Socket(Inet4Address.getByName(hostname), port);

            logger.info("Initializing the input and output streams...");
            this.outputStream = connection.getOutputStream();
            this.outputStream.flush();
            this.inputStream = connection.getInputStream();

            this.finished = false;

            logger.info("Starting the reader thread...");
            this.startReader();
        } catch (Exception e) {
            logger.error("Error initializing the connection: {}", e.getMessage());
            throw new AppException("Error initializing the connection: " + e.getMessage());
        }
    }

    /**
     * Closes the connection with the server.
     */
    private void closeConnection() {
        logger.traceEntry("Closing the connection...");
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.connection.close();
        } catch (Exception e) {
            logger.error("Error closing the connection: {}", e.getMessage());
        }
    }

    /**
     * Sends a request to the server.
     *
     * @param request the request to be sent.
     * @throws AppException if an error occurs while sending the request.
     */
    private void sendRequest(NetworkProtos.Request request) throws AppException {
        logger.traceEntry("Sending the request: {}", request.toString());

        try {
            request.writeDelimitedTo(outputStream);
            outputStream.flush();
            logger.traceExit("Request sent successfully!");
        } catch (Exception e) {
            logger.error("Error sending the request: {}", e.getMessage());
            throw new AppException("Error sending the request: " + e.getMessage());
        }
    }

    /**
     * Reads a response from the server.
     *
     * @return the response read.
     * @throws AppException if an error occurs while reading the response.
     */
    private NetworkProtos.Response readResponse() throws AppException {
        logger.traceEntry("Reading the response...");
        try {
            NetworkProtos.Response response = responses.take();
            logger.traceExit("Response read successfully!");
            return response;
        } catch (Exception e) {
            logger.error("Error reading the response: {}", e.getMessage());
            throw new AppException("Error reading the response: " + e.getMessage());
        }
    }

    /**
     * Handles an update response.
     *
     * @param response the response to be handled.
     */
    private void handleUpdate(NetworkProtos.Response response) throws AppException {
        logger.traceEntry("Handling the update response...");

        String handlerName = "handle" + response.getResponseType().name();
        try {
            logger.info("Invoking the handler method: {}", handlerName);
            Method handleMethod = this.getClass().getDeclaredMethod(handlerName, NetworkProtos.Response.class);
            handleMethod.invoke(this, response);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.error("Error handling the update response: {}", e.getMessage());
            throw new AppException("Error handling the update response: " + e.getMessage());
        }

        logger.traceExit("Update response handled successfully!");
    }

    private void handlePARTICIPANT_ADDED(NetworkProtos.Response response) throws AppException {
        logger.traceEntry("Handling the PARTICIPANT_ADDED response...");
        Participant participant = new Participant.Builder()
                .setId(UUID.fromString(response.getParticipant().getId().getValue()))
                .setFirstName(response.getParticipant().getFirstName())
                .setLastName(response.getParticipant().getLastName())
                .setTeam(response.getParticipant().getTeam())
                .setEngineCapacity(response.getParticipant().getEngineCapacity())
                .build();
        try {
            logger.info("Notifying the client...");
            client.participantAdded(participant);
        } catch (AppException e) {
            logger.error("Error notifying the client: {}", e.getMessage());
            throw new AppException("Error notifying the client: " + e.getMessage());
        }
        logger.traceExit("PARTICIPANT_ADDED response handled successfully!");
    }

    /**
     * Checks if a response is an update, not made by the current client.
     *
     * @param response the response to be checked.
     * @return true if the response is an update, false otherwise.
     */
    private boolean isUpdate(NetworkProtos.Response response) {
        return response.getResponseType() == NetworkProtos.Response.type.PARTICIPANT_ADDED;
    }

    /**
     * Checks if the server is closed.
     * @param response the response to be checked.
     * @return true if the server is closed, false otherwise.
     */
    private boolean isServerClosed(NetworkProtos.Response response) {
        return response.getResponseType() == NetworkProtos.Response.type.CONNECTION_CLOSED;
    }

    /**
     * Starts the reader thread.
     */
    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    /**
     * The reader thread.
     */
    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    NetworkProtos.Response response = NetworkProtos.Response.parseDelimitedFrom(inputStream);
                    logger.info("Received response: {}", response);

                    if (response != null) {
                        if (isServerClosed(response)) {
                            logger.warn("The server has been closed!");
                            try {
                                closeConnection();
                                client.shutdownGUI();
                                finished = true;
                                break;
                            } catch (Exception e) {
                                logger.error("Error closing the connection: {}", e.getMessage());
                            }
                        }
                        // Updating the client if the response was not directed to the current client.
                        if (isUpdate(response)) {
                            logger.info("The passed response is an update.");
                            handleUpdate(response);
                        } else {
                            // Adding the response into the queue.
                            logger.info("Saving the response.");
                            responses.put(response);
                        }
                    } else {
                        logger.error("Couldn't receive the response from the server!");
                        NetworkProtos.Response errorResponse = ProtocolUtils.createErrorResponse("Couldn't receive the response from the server!");
                        responses.put(errorResponse);
                    }
                } catch (Exception e) {
                    logger.error("Error reading from the input stream: {}", e.getMessage());
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.error("Error sleeping the thread: {}", e.getMessage());
                }
            }
            closeConnection();
        }
    }

    @Override
    public Iterable<Participant> findParticipantsByTeam(String team) throws AppException {
        logger.traceEntry("Finding participants by team: {}", team);
        try {
            logger.info("Sending the request...");
            NetworkProtos.Request request = ProtocolUtils.createFindParticipantsByTeamRequest(team);
            this.sendRequest(request);

            logger.info("Reading the response...");
            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.traceExit("Participants found successfully!");
                return response.getParticipantsList().stream()
                        .map(participant -> new Participant.Builder()
                                .setId(UUID.fromString(participant.getId().getValue()))
                                .setFirstName(participant.getFirstName())
                                .setLastName(participant.getLastName())
                                .setTeam(participant.getTeam())
                                .setEngineCapacity(participant.getEngineCapacity())
                                .build())
                        .toList();
            }
            logger.error("Error finding participants by team: {}", response.getErrorMessage());
            throw new AppException("Error finding participants by team: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error finding participants by team: {}", e.getMessage());
            throw new AppException("Error finding participants by team: " + e.getMessage());
        }
    }

    @Override
    public void addParticipant(String firstName, String lastName, String team, int engineCapacity) throws ServiceException {
        logger.traceEntry("Adding participant with firstName: {}, lastName: {}, team: {}, engineCapacity: {}",
                firstName, lastName, team, engineCapacity);
        try {
            logger.info("Sending the request...");
            NetworkProtos.Request request = ProtocolUtils.createAddParticipantRequest(firstName, lastName, team, engineCapacity);
            this.sendRequest(request);

            logger.info("Reading the response...");
            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.traceExit("Participant added successfully!");
                return;
            }
            logger.error("Error adding participant: {}", response.getErrorMessage());
            throw new ServiceException("Error adding participant: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error adding participant: {}", e.getMessage());
            throw new ServiceException("Error adding participant: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Race> findAllRaces() throws ServiceException {
        logger.traceEntry("Finding all the races...");
        try {
            logger.info("Sending the request...");
            NetworkProtos.Request request = ProtocolUtils.createFindAllRacesRequest();
            this.sendRequest(request);

            logger.info("Reading the response...");
            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.traceExit("Returning the races...");
                return response.getRacesList().stream()
                        .map(race -> new Race.Builder()
                                .setId(race.getId())
                                .setEngineCapacity(race.getEngineCapacity())
                                .setNoParticipants(race.getNoParticipants())
                                .build())
                        .toList();
            }
            logger.error("Error in finding all the races: {}", response.getErrorMessage());
            throw new ServiceException("Error in finding all the races: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error in finding all the races: {}", e.getMessage());
            throw new ServiceException("Error in finding all the races: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Integer> findAllRaceEngineCapacities() throws ServiceException {
        logger.traceEntry("Finding all the engine capacities...");
        try {
            logger.info("Sending the request...");
            NetworkProtos.Request request = ProtocolUtils.createFindAllRaceEngineCapacitiesRequest();
            this.sendRequest(request);

            logger.info("Reading the response...");
            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.traceExit("Returning the races...");
                return response.getEngineCapacitiesList();
            }
            logger.error("Error in finding all the engine capacities: {}", response.getErrorMessage());
            throw new ServiceException("Error in finding all the engine capacities: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error in finding all the engine capacities: {}", e.getMessage());
            throw new ServiceException("Error in finding all the engine capacities: " + e.getMessage());
        }
    }

    @Override
    public User login(String username, String password, Observer client) throws ServiceException {
        logger.traceEntry("Logging in with username: {}.", username);
        try {
            logger.info("Initializing the connection...");
            this.initializeConnection();

            logger.info("Sending the login request...");
            NetworkProtos.Request request = ProtocolUtils.createLoginRequest(username, password);
            this.sendRequest(request);

            logger.info("Reading the response from the server...");
            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.info("Logged in successfully!");

                return new User.Builder()
                        .setId(response.getUser().getId())
                        .setFirstName(response.getUser().getFirstName())
                        .setLastName(response.getUser().getLastName())
                        .setUsername(response.getUser().getUsername())
                        .build();
            }
            logger.error("Error logging in: {}", response.getErrorMessage());
            this.finished = true;
            throw new ServiceException("Error logging in: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error initializing the connection: {}", e.getMessage());
            this.finished = true;
            throw new ServiceException("Error initializing the connection: " + e.getMessage());
        }
    }

    @Override
    public void logout(User user, Observer client) throws AppException {
        logger.traceEntry("Logging out user: {}", user.getUsername());

        try {
            logger.info("Sending the logout request...");
            NetworkProtos.Request request = ProtocolUtils.createLogoutRequest(user);
            this.sendRequest(request);

            NetworkProtos.Response response = this.readResponse();

            if (response.getResponseType() == NetworkProtos.Response.type.OK) {
                logger.info("Logged out successfully!");

                logger.info("Closing the connection...");
                this.finished = true;

                return;
            }

            logger.error("Error logging out: {}", response.getErrorMessage());
            throw new AppException("Error logging out: " + response.getErrorMessage());
        } catch (AppException e) {
            logger.error("Error sending the logout request: {}", e.getMessage());
            throw new AppException("Error sending the logout request: " + e.getMessage());
        }
    }
}
