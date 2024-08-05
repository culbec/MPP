package NetworkUtils;

import java.net.Socket;

public abstract class AbstractConcurrentServer extends AbstractServer {
    public AbstractConcurrentServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected void processRequest(Socket client) throws ServerException {
        logger.traceEntry("Processing the request of the client at: {}", client.getInetAddress());

        // Creating a thread that will server the client.
        logger.info("Creating a new worker for the client...");
        Thread worker = createWorker(client);

        // Starting the worker.
        logger.info("Starting the worker...");
        worker.start();

        logger.traceExit();
    }

    /**
     * Creates a new worker for the client to concurrently process the request.
     * @param client The client to create the worker for.
     * @return The worker for the client.
     */
    protected abstract Thread createWorker(Socket client) throws ServerException;
}
