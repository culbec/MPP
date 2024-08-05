import CommonUtils.IService;
import NetworkUtils.AbstractServer;
import NetworkUtils.ProtobufServer;
import NetworkUtils.ServerException;
import Repository.IUserRepository;
import Repository.UserRepository;
import Service.IServiceUser;
import Service.Service;
import Service.ServiceUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class StartServer {
    private static final Logger logger = LogManager.getLogger(StartServer.class);
    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        logger.traceEntry("Starting the server...");

        logger.info("Loading the server configuration...");
        Properties properties = new Properties();

        try {
            properties.load(StartServer.class.getResourceAsStream("/server.properties"));

            logger.info("Properties loaded successfully.");
            //properties.list(logger);
        } catch (Exception e) {
            logger.error("Error loading the server configuration: {}", e.getMessage());
            return;
        }

        logger.info("Retrieving the host and port of the server from the configuration...");
        String hostname = properties.getProperty("server.host", DEFAULT_HOST);
        int port = DEFAULT_PORT;

        try {
            port = Integer.parseInt(properties.getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("Invalid port number. Using the default port: {}", DEFAULT_PORT);
        }

        logger.info("Initializing the server.");
        IUserRepository userRepository = new UserRepository();
        IServiceUser serviceUser = new ServiceUser(userRepository);
        IService service = new Service(serviceUser);

        logger.info("Starting the server on {}:{}", hostname, port);

        AbstractServer server = new ProtobufServer(hostname, port, service);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error when trying to start the server: {}", e.getMessage());
        }
    }
}
