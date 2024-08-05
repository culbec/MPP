package CommonUtils;

import Exceptions.AppException;
import Exceptions.ServiceException;
import Model.User;

public interface IService {
    /**
     * Logins a user into the app.
     *
     * @param username Username passed by the user.
     * @param password Password passed by the user.
     * @param client Client that requested the login.
     * @return The user with the passed credentials.
     * @throws ServiceException If the user doesn't exist or the password is incorrect.
     */
    User login(String username, String password, IObserver client) throws ServiceException, AppException;

    /**
     * Logs out a user from the app.
     * @param user User to be logged out.
     * @param client Client that requested the logout;
     */
    void logout(User user, IObserver client) throws ServiceException, AppException;
}
