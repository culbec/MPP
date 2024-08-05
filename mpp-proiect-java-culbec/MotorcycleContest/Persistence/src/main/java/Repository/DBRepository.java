package Repository;

import Model.Entity;
import ConnectionUtils.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public abstract class DBRepository<ID, E extends Entity<ID>> {
    protected final Logger logger;
    protected final DBUtils dbUtils;
    // Map containing the prepared statements characteristic for the repository.
    protected final Map<String, PreparedStatement> preparedStatementMap = new HashMap<>();

    public DBRepository(DBUtils dbUtils) throws RepositoryException {
        logger = LogManager.getLogger(this.getClass()); // creating a logger for the current class

        logger.traceEntry("Initializing the DB utils and the prepared statements...");
        this.dbUtils = dbUtils;
        this.initPreparedStatements();

        logger.traceExit("Initialized the DB utils and the prepared statements!");
    }

    /**
     * Initializes the map of prepared statements.
     */
    protected abstract void initPreparedStatements() throws RepositoryException;

    /**
     * Extracts an entity from a given result set.
     *
     * @param resultSet {@code ResultSet} to extract the entity from.
     * @return An {@code Optional} containing the entity extracted, or an empty one if the entity couldn't be extracted.
     */
    protected abstract E extractFromResultSet(ResultSet resultSet) throws RepositoryException;
}
