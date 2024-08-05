package Workers;

import CommonUtils.IObserver;
import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
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

public class RPCReflectionWorker implements Runnable, IObserver {
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
            User user = new User(userProto.getUsername(), userProto.getPassword(), userProto.getFirstName(), userProto.getLastName());
            user.setId(userProto.getId());

            this.server.logout(user, this);
            this.connected = false;
            return ProtocolUtils.createLogoutResponse();
        } catch (ServiceException | AppException e) {
            logger.error("Error when logging out: {}", e.getMessage());
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
}
