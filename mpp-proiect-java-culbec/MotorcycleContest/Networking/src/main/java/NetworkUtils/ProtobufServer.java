package NetworkUtils;

import CommonUtils.IService;
import Workers.RPCReflectionWorker;

import java.net.Socket;

public class ProtobufServer extends AbstractConcurrentServer {
    private final IService server;

    public ProtobufServer(String hostname, int port, IService server) {
        super(hostname, port);
        this.server = server;
    }

    @Override
    protected Thread createWorker(Socket client) throws ServerException {
        // Creating a worker specific for this type of server.
        logger.traceEntry("Creating a new worker for the client...");

        try {
            RPCReflectionWorker worker = new RPCReflectionWorker(server, client);
            logger.traceExit("Worker created.");
            return new Thread(worker);
        } catch (Exception e) {
            logger.error("Couldn't create the worker: {}", e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
