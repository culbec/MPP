package Repository;


import ConnectionUtils.DBUtils;
import Model.Race;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RaceDBRepository extends DBRepository<Integer, Race> implements RaceRepository {
    public RaceDBRepository(DBUtils dbUtils) throws RepositoryException {
        super(dbUtils);
    }

    @Override
    protected void initPreparedStatements() throws RepositoryException {
        logger.traceEntry("Initializing the prepared statements for the RaceDBRepository...");

        String sqlSelectAllEngineCapacities = "select r.engine_capacity from races r order by r.engine_capacity";
        String sqlSelectAll = "select r.rid, r.engine_capacity, count(p.engine_capacity) as no_participants from races r left join participants p on r.engine_capacity = p.engine_capacity group by r.engine_capacity, r.rid order by r.engine_capacity";

        logger.info("Trying to establish a connection with the database...");
        Connection connection = this.dbUtils.getConnection();

        if (connection == null) {
            logger.error("Couldn't establish a connection with the database!");
            throw new RepositoryException("Couldn't establish the connection with the database!");
        }

        logger.info("Inserting the prepared statements...");
        try {
            this.preparedStatementMap.put("selectAllEngineCapacities", connection.prepareStatement(sqlSelectAllEngineCapacities));
            this.preparedStatementMap.put("selectAll", connection.prepareStatement(sqlSelectAll));
        } catch (SQLException e) {
            logger.error("Couldn't insert the prepared statements: {}", e.getMessage());
            throw new RepositoryException("Couldn't initialize the prepared statements for the RaceDBRepository: " + e.getMessage());
        }

        logger.traceExit("Initialized the prepared statements for the RaceDBRepository!");
    }

    @Override
    protected Race extractFromResultSet(ResultSet resultSet) throws RepositoryException {
        logger.traceEntry("Extracting the race from the result set...");

        try {
            Integer rid = resultSet.getInt("rid");
            Integer engineCapacity = resultSet.getInt("engine_capacity");
            Integer noParticipants = resultSet.getInt("no_participants");

            logger.traceExit("Extracted the race from the result set!");
            return new Race.Builder()
                    .setId(rid)
                    .setEngineCapacity(engineCapacity)
                    .setNoParticipants(noParticipants)
                    .build();
        } catch (SQLException e) {
            logger.error("Couldn't extract the race from the result set: {}", e.getMessage());
            throw new RepositoryException("Couldn't extract the race from the result set: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Integer> findAllRaceEngineCapacities() throws RepositoryException {
        logger.traceEntry("Finding all engine capacities...");
        List<Integer> engineCapacities = new ArrayList<>();

        try (ResultSet resultSet = this.preparedStatementMap.get("selectAllEngineCapacities").executeQuery()) {
            logger.info("Statement executed successfully!");

            while (resultSet.next()) {
                Integer engineCapacity = resultSet.getInt("engine_capacity");
                engineCapacities.add(engineCapacity);
            }

            logger.traceExit("Found all the engine capacities!");
            return engineCapacities;

        } catch (SQLException e) {
            logger.error("Couldn't find the engine capacities: {}", e.getMessage());
            throw new RepositoryException("Couldn't get the engine capacities: " + e.getMessage());
        }
    }

    @Override
    public Optional<Race> findOne(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Iterable<Race> findAll() throws RepositoryException {
        logger.traceEntry("Finding all the races...");

        List<Race> races = new ArrayList<>();

        try {
            ResultSet resultSet = this.preparedStatementMap.get("selectAll").executeQuery();

            logger.info("Statement executed successfully!");

            while (resultSet.next()) {
                Race race = this.extractFromResultSet(resultSet);
                races.add(race);
            }

            logger.traceExit("Found all the races!");
            return races;
        } catch (SQLException e) {
            logger.error("Couldn't find all the races: {}", e.getMessage());
            throw new RepositoryException("Couldn't find all the races: " + e.getMessage());
        }
    }

    @Override
    public Optional<Race> save(Race entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Race> delete(Race entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Race> update(Race entity) {
        return Optional.empty();
    }
}
