package Repository;

import ConnectionUtils.DBUtils;
import Model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDBRepository extends DBRepository<Integer, User> implements UserRepository {
    public UserDBRepository(DBUtils dbUtils) throws RepositoryException {
        super(dbUtils);
    }

    @Override
    protected void initPreparedStatements() throws RepositoryException {
        logger.traceEntry("Initializing the prepared statements for the Model.User Repository...");

        String sqlSelectOnUsername = "select * from users u where u.username = ?";

        logger.info("Trying to establish a connection with the database...");
        Connection connection = this.dbUtils.getConnection();

        if (connection == null) {
            logger.error("Couldn't establish the connection with the database!");
            throw new RepositoryException("Couldn't establish the connection with the database!");
        }

        logger.info("Inserting the prepared statements...");
        try {
            this.preparedStatementMap.put("selectOnUsername", connection.prepareStatement(sqlSelectOnUsername));
        } catch (SQLException e) {
            logger.error("Couldn't insert the prepared statements: {}", e.getMessage());
            throw new RepositoryException("Couldn't initialize the prepared statements for the Model.User Repository: " + e.getMessage());
        }

        logger.traceExit("Initialized the prepared statements for the Model.User Repository!");
    }

    @Override
    protected User extractFromResultSet(ResultSet resultSet) throws RepositoryException {
        logger.traceEntry("Extracting the user from the result set...");

        try {
            Integer uid = resultSet.getInt("uid");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String username = resultSet.getString("username");

            User user = new User.Builder()
                    .setId(uid)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setUsername(username)
                    .build();
            logger.traceExit("Extracted the user from the result set!");
            return user;
        } catch (SQLException e) {
            logger.error("Couldn't extract the user from the result set: {}", e.getMessage());
            throw new RepositoryException("Couldn't extract the user from the result set: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findOne(Integer id) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public Optional<User> save(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> delete(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByCredentials(String username, String password) throws RepositoryException {
        logger.traceEntry("Trying to find the user with the credentials {} {} ", username, password);

        try {
            PreparedStatement preparedStatement = this.preparedStatementMap.get("selectOnUsername");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String _password = resultSet.getString("password");

                User user = this.extractFromResultSet(resultSet);

                if (user != null && checkPassword(password, _password)) {
                    logger.traceExit("Found the user with the credentials: " + username + " - " + password);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Couldn't find the user with the credentials: {} - {} - {}", username, password, e.getMessage());
            throw new RepositoryException("Couldn't find the user with the credentials: " + username + " - " + password + " - " + e.getMessage());
        }

        logger.traceExit("Couldn't find the user with the credentials: " + username + " - " + password);
        return Optional.empty();
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

}
