package ConnectionUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils implements AutoCloseable {
    // Logger util for logging the actions.
    private static final Logger logger = LogManager.getLogger();
    // Properties of the database.
    private final Properties DBProperties;
    // Connection instance.
    private Connection connection = null;

    public DBUtils(Properties DBProperties) {
        this.DBProperties = DBProperties;
    }

    /**
     * Creates a new connection to the database or returns the existing one.
     *
     * @return A connection to the database.
     */
    public Connection getConnection() {
        logger.traceEntry("Trying to get the connection...");

        if (connection != null) {
            logger.traceExit("Connection found as initiated. Returning...");
            return connection;
        }

        logger.info("Getting DB properties...");
        String url = DBProperties.getProperty("mariadb.jdbc.url");
        String user = DBProperties.getProperty("mariadb.jdbc.user");
        String pass = DBProperties.getProperty("mariadb.jdbc.pass");

        logger.info("Trying to login...");
        logger.info("Username: {}", user);
        logger.info("Password: {}", pass);

        try {
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            logger.error("Error when trying to connect: {}", e.getMessage());
        }

        logger.traceExit("A new connection was initiated! Returning with: " + this.connection);
        return this.connection;
    }

    @Override
    public void close() throws IOException {
        logger.traceEntry("Closing the connection...");
        try {
            if (this.connection != null) {
                this.connection.close();
                logger.info("Connection closed successfully.");
            }
        } catch (Exception e) {
            logger.error("Error when trying to close the connection: {}", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }
}
