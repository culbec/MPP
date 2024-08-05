package Repository;

import Model.User;

import java.util.Optional;

/**
 * Interface for the user repository.
 */
public interface UserRepository extends Repository<Integer, User> {
    /**
     * Finds a user by the passed credentials.
     *
     * @param username Username of the user.
     * @param password Password of the user.
     * @return An {@code Optional} containing the user if the user has been found.
     */
    Optional<User> findUserByCredentials(String username, String password) throws RepositoryException;
}
