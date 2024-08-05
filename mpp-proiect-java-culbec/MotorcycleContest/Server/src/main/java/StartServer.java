import CommonUtils.IService;
import ConnectionUtils.DBUtils;
import NetworkUtils.AbstractServer;
import NetworkUtils.ProtobufServer;
import NetworkUtils.ServerException;
import Repository.*;
import Service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static final Logger logger = LogManager.getLogger(StartServer.class);
    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) throws RepositoryException {
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

        logger.info("Initializing a DBUtils object...");
        try (DBUtils dbUtils = new DBUtils(properties)) {
            logger.info("Initializing the repositories...");
            UserRepository userRepository = new UserDBRepository(dbUtils);
            ParticipantRepository participantRepository = new ParticipantDBRepository(dbUtils);
            RaceRepository raceRepository = new RaceDBRepository(dbUtils);

            logger.info("Initializing the server...");

            ServiceUser serviceUser = new ServiceUserImpl(userRepository);
            ServiceParticipant serviceParticipant = new ServiceParticipantImpl(participantRepository);
            ServiceRace serviceRace = new ServiceRaceImpl(raceRepository);
            IService service = new ServiceImpl(serviceUser, serviceParticipant, serviceRace);

            logger.info("Retrieving the host and port of the server from the configuration...");
            String hostname = properties.getProperty("server.host", DEFAULT_HOST);
            int port = DEFAULT_PORT;

            try {
                port = Integer.parseInt(properties.getProperty("server.port"));
            } catch (NumberFormatException e) {
                logger.error("Invalid port number. Using the default port: {}", DEFAULT_PORT);
            }

            logger.info("Starting the server on {}:{}", hostname, port);

            AbstractServer server = new ProtobufServer(hostname, port, service);
            try {
                server.start();
            } catch (ServerException e) {
                logger.error("Error when trying to start the server: {}", e.getMessage());
            }

        } catch (IOException e) {
            logger.error("Error when trying to close the connection: {}", e.getMessage());
        }
    }
}
