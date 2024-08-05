package Repository;

import Model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserRepository implements IUserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public UserRepository() {

    }

    @Override
    public Optional<User> findUserByCredentials(String username, String password) throws RepositoryException {
        logger.traceEntry("Finding user with username: {}", username);
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            Optional<User> found = session.createSelectionQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();

            if (found.isEmpty()) {
                logger.error("The user with username: {} doesn't exist.", username);
                return Optional.empty();
            }

            if (!checkPassword(password, found.get().getPassword())) {
                logger.error("The password is incorrect.");
                return Optional.empty();
            }

            logger.traceExit("Found the user with username: {}", username);
            return found;
        }
    }

    /**
     * Checks if the passed password is the same as the encrypted one.
     *
     * @param password       Password to be checked.
     * @param hashedPassword Encrypted password.
     * @return {@code true} if the password is the same as the encrypted one, {@code false} otherwise.
     */
    private boolean checkPassword(String password, String hashedPassword) {
        logger.traceEntry("Checking the password: " + password + " - " + hashedPassword);
        logger.traceExit("Checked the password: " + password + " - " + hashedPassword);
        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public Optional<User> findOne(Long aLong) throws RepositoryException {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            logger.info("Retrieving user with id: {}", aLong);
            return session.createSelectionQuery("from User where id = :id", User.class)
                    .setParameter("id", aLong)
                    .uniqueResultOptional();
        }
    }

    @Override
    public Iterable<User> findAll() throws RepositoryException {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            logger.info("Retrieving all users.");
            return session.createSelectionQuery("from User", User.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<User> save(User user) throws RepositoryException {
        logger.info("Saving the user: {}", user);
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(user));
        return Optional.of(user);
    }

    @Override
    public Optional<User> delete(Long aLong) {
        AtomicReference<Optional<User>> old = new AtomicReference<>(Optional.empty());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            User oldUser = session.get(User.class, aLong);

            if (!Objects.isNull(oldUser)) {
                logger.info("Deleting the user with id: {}", aLong);
                session.remove(oldUser);
                session.flush();
                old.set(Optional.of(oldUser));
            } else {
                logger.warn("Couldn't find the user with id: {}", aLong);
            }
        });

        return old.get();
    }

    @Override
    public Optional<User> update(User user) throws RepositoryException {
        AtomicReference<Optional<User>> old = new AtomicReference<>(Optional.empty());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            User oldUser = session.get(User.class, user.getId());

            if (!Objects.isNull(oldUser)) {
                logger.info("Updating the user with id: {}", user.getId());
                session.merge(oldUser);
                session.flush();
                old.set(Optional.of(oldUser));
            } else {
                logger.warn("Couldn't find the user with id: {}", user.getId());
            }
        });

        return old.get();
    }
}
