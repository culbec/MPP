package Service;

import CommonUtils.IObserver;
import CommonUtils.IService;
import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IService {
    private static final Logger logger = LogManager.getLogger(Service.class);
    private final IServiceUser serviceUser;
    private final Map<String, IObserver> loggedClients = new ConcurrentHashMap<>();

    private static final int DEFAULT_THREADS_NO = 3;

    public Service(IServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @Override
    public synchronized User login(String username, String password, IObserver client) throws ServiceException, AppException {
        logger.traceEntry("Trying to login the user {}", username);

        logger.info("Verifying if the user is already logged in...");
        if (loggedClients.get(username) != null) {
            logger.error("The client is already logged in!");
            throw new ServiceException("The client is already logged in!");
        }

        try {
            logger.info("Trying to retrieve the user by the passed credentials...");
            User user = this.serviceUser.login(username, password);

            logger.info("User found. Updating the logged in clients...");
            loggedClients.put(username, client);

            logger.traceExit("Returning the user...");
            return user;
        } catch (ServiceException e) {
            logger.error("Couldn't login the user: {}", e.getMessage());
            throw new ServiceException("Couldn't login the user: " + e.getMessage());
        }
    }

    @Override
    public synchronized void logout(User user, IObserver client) throws ServiceException, AppException {
        logger.traceEntry("Logging out the user: {}", user);

        IObserver localClient = this.loggedClients.remove(user.getUsername());
        if (localClient == null) {
            logger.error("The user is not logged in!");
            throw new ServiceException("The user is not logged in!");
        }

        logger.traceExit("The user was logged out.");
    }
}
