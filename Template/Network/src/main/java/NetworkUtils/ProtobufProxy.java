package NetworkUtils;

import CommonUtils.IGUIObserver;
import CommonUtils.IObserver;
import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtobufProxy implements IService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final String hostname;
    private final int port;
    private IGUIObserver client;

    private Socket connection;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final BlockingQueue<NetworkProtos.Response> responses = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public ProtobufProxy(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void setClient(IObserver client) {
        this.client = (IGUIObserver) client;
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

    @Override
    public User login(String username, String password, IObserver client) throws ServiceException, AppException {
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

                NetworkProtos.User userProto = response.getUser();
                User user = new User(userProto.getUsername(), userProto.getPassword(), userProto.getFirstName(), userProto.getLastName());
                user.setId(userProto.getId());
                return user;
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
    public void logout(User user, IObserver client) throws ServiceException, AppException {
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
