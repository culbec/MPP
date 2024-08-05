package Repository;

import Model.User;

import java.util.Optional;

public interface IUserRepository extends IRepository<Long, User> {
    /**
     * Finds a user by the passed credentials.
     *
     * @param username Username of the user.
     * @param password Password of the user.
     * @return An {@code Optional} containing the user if the user has been found.
     */
    Optional<User> findUserByCredentials(String username, String password) throws RepositoryException;
}
