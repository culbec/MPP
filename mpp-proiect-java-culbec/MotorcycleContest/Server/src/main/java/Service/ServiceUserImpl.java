package Service;

import Repository.RepositoryException;
import Exceptions.ServiceException;
import Model.User;
import Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ServiceUserImpl implements ServiceUser {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UserRepository userRepository;

    public ServiceUserImpl(UserRepository userRepository) {
        LOGGER.traceEntry("Initializing the ServiceUserImpl with the UserRepository...");
        this.userRepository = userRepository;
        LOGGER.traceExit("Initialized the ServiceUserImpl with the UserRepository!");
    }

    @Override
    public User login(String username, String password) throws ServiceException {
        LOGGER.traceEntry("Logging in the user with the username {}...", username);
        try {
            Optional<User> user = userRepository.findUserByCredentials(username, password);

            if (user.isEmpty()) {
                LOGGER.error("The user with the username {} doesn't exist or the password is incorrect!", username);
                throw new ServiceException("The user doesn't exist or the password is incorrect!");
            }

            LOGGER.traceExit("Logged in the user with the username {}!", username);
            return user.get();
        } catch (RepositoryException e) {
            LOGGER.error("Couldn't login the user with the username {}: {}", username, e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }
}
