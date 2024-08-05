package NetworkUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    protected final Logger logger;
    private static final int DEFAULT_BACKLOG = 10;

    private final String hostname;
    private final int port;
    private ServerSocket server = null;

    public AbstractServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.logger = LogManager.getLogger(this.getClass());
    }

    /**
     * Processes a request of a client.
     * @param client Client that sent the request.
     * @throws ServerException If the server encountered a problem on processing the request.
     */
    protected abstract void processRequest(Socket client) throws ServerException;

    /**
     * Starts the server.
     * @throws ServerException If the server encountered a problem on starting.
     */
    public void start() throws ServerException {
        logger.traceEntry("Starting the server...");
        try {
            // Creating a server socket.
            InetAddress address = Inet4Address.getByName(this.hostname);
            this.server = new ServerSocket(port, DEFAULT_BACKLOG, address);

            logger.info("Server started on port: {}", port);

            // Accepting clients indefinitely.
            while (true) {
                Socket client = server.accept();
                logger.info("Client connected: {}", client.getInetAddress());

                logger.info("Processing the request...");
                this.processRequest(client);
            }
        } catch (IOException e) {
            logger.error("Couldn't start the server: {}", e.getMessage());
            throw new ServerException(e.getMessage());
        } finally {
            // Stopping the server after crashing or after explicitly stopping it.
            this.stop();
        }
    }

    /**
     * Stops the server.
     * @throws ServerException If the server encountered a problem on stopping.
     */
    public void stop() throws ServerException {
        logger.traceEntry("Stopping the server...");
        try {
            // Closing the server socket.
            this.server.close();
        } catch (IOException e) {
            logger.error("Couldn't stop the server: {}", e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
