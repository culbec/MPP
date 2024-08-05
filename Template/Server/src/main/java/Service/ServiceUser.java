package Service;

import Exceptions.ServiceException;
import Model.User;
import Repository.IUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ServiceUser implements IServiceUser {
    private static final Logger logger = LogManager.getLogger();
    private final IUserRepository userRepository;

    public ServiceUser(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User login(String username, String password) throws ServiceException {
        logger.traceEntry("Login - username: {}", username);
        try {
            Optional<User> userOptional = userRepository.findUserByCredentials(username, password);

            if (userOptional.isEmpty()) {
                logger.error("The user doesn't exist or the password is incorrect.");
                throw new ServiceException("The user doesn't exist or the password is incorrect.");
            }

            logger.traceExit("User found: {}", userOptional.get());
            return userOptional.get();
        } catch (Exception exception) {
            logger.error("Couldn't login the user with username: {}. Error: {}", username, exception.getMessage());
            throw new ServiceException("Couldn't login the user.");
        }
    }


}
