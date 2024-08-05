package Service;

import Exceptions.ServiceException;
import Model.User;

public interface IServiceUser {
    /**
     * Logins a user into the app.
     *
     * @param username Username passed by the user.
     * @param password Password passed by the user.
     * @return The user with the passed credentials.
     * @throws ServiceException If the user doesn't exist or the password is incorrect.
     */
    User login(String username, String password) throws ServiceException;
}
