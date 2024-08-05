package Repository;

import ConnectionUtils.DBUtils;
import Model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ParticipantDBRepository extends DBRepository<UUID, Participant> implements ParticipantRepository {
    public ParticipantDBRepository(DBUtils dbUtils) throws RepositoryException {
        super(dbUtils);
    }

    @Override
    protected Participant extractFromResultSet(ResultSet resultSet) throws RepositoryException {
        logger.traceEntry("Extracting the participant from the result set...");

        try {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String team = resultSet.getString("team");
            Integer engineCapacity = resultSet.getInt("engine_capacity");

            Participant participant = new Participant.Builder()
                    .setId(UUID.randomUUID())
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setTeam(team)
                    .setEngineCapacity(engineCapacity)
                    .build();
            logger.traceExit("Extracted the participant from the result set: " + participant);
            return participant;
        } catch (SQLException e) {
            logger.error("Couldn't extract the participant from the result set: {}", e.getMessage());
            throw new RepositoryException("Couldn't extract the participant from the result set: " + e.getMessage());
        }
    }

    @Override
    protected void initPreparedStatements() throws RepositoryException {
        logger.traceEntry("Initializing the prepared statements for the Model.Participant Repository...");

        String sqlSelectOnTeam = "select * from participants p where p.team = ?";
        String sqlSelectOnFields = "select * from participants p where p.first_name = ? and p.last_name = ? and p.team = ? and p.engine_capacity = ?";
        String sqlCountOnEngineCapacity = "select count(*) from participants p where p.engine_capacity = ?";
        String sqlInsert = "insert into participants(first_name, last_name, team, engine_capacity) values(?, ?, ?, ?)";

        logger.info("Trying to establish a connection with the database...");
        Connection connection = this.dbUtils.getConnection();

        if (connection == null) {
            logger.error("Couldn't establish the connection with the database!");
            throw new RepositoryException("Couldn't establish the connection with the database!");
        }

        logger.info("Inserting the prepared statements...");
        try {
            this.preparedStatementMap.put("selectOnTeam", connection.prepareStatement(sqlSelectOnTeam));
            this.preparedStatementMap.put("selectOnFields", connection.prepareStatement(sqlSelectOnFields));
            this.preparedStatementMap.put("countOnEngineCapacity", connection.prepareStatement(sqlCountOnEngineCapacity));
            this.preparedStatementMap.put("insert", connection.prepareStatement(sqlInsert));
        } catch (SQLException e) {
            logger.error("Couldn't insert the prepared statements: {}", e.getMessage());
            throw new RepositoryException("Couldn't initialize the prepared statements for the Model.Participant Repository: " + e.getMessage());
        }

        logger.traceExit("Initialized the prepared statements for the Participants Repository!");
    }

    @Override
    public Optional<Participant> findOne(UUID id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Participant> findAll() {
        return null;
    }

    @Override
    public Optional<Participant> save(Participant participant) throws RepositoryException {
        logger.traceEntry("Trying to save the participant {}", participant);

        logger.info("Checking if the participant already exists...");
        try {
            Optional<Participant> found = this.findParticipantByFields(participant);

            if (found.isPresent()) {
                logger.error("The participant already exists!");
                return Optional.empty();
            }
        } catch (RepositoryException e) {
            logger.error("Couldn't find the participant: {}", e.getMessage());
            throw new RepositoryException("Couldn't find the participant!");
        }

        try {
            PreparedStatement preparedStatement = this.preparedStatementMap.get("insert");
            preparedStatement.setString(1, participant.getFirstName());
            preparedStatement.setString(2, participant.getLastName());
            preparedStatement.setString(3, participant.getTeam());
            preparedStatement.setInt(4, participant.getEngineCapacity());

            preparedStatement.executeUpdate();
            logger.traceExit("Saved the participant: " + participant);
            return Optional.of(participant);
        } catch (SQLException e) {
            logger.error("Couldn't save the participant: {} - {}", participant, e.getMessage());
            throw new RepositoryException("Couldn't save the participant: " + participant + " - " + e.getMessage());
        }
    }

    @Override
    public Optional<Participant> delete(Participant participant) {
        return Optional.empty();
    }

    @Override
    public Optional<Participant> update(Participant participant) {
        return Optional.empty();
    }

    @Override
    public Optional<Participant> findParticipantByFields(Participant participant) throws RepositoryException {
        logger.traceEntry("Trying to find the participant by the fields: " + participant);

        try {
            PreparedStatement preparedStatement = this.preparedStatementMap.get("selectOnFields");
            preparedStatement.setString(1, participant.getFirstName());
            preparedStatement.setString(2, participant.getLastName());
            preparedStatement.setString(3, participant.getTeam());
            preparedStatement.setInt(4, participant.getEngineCapacity());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Participant _participant = this.extractFromResultSet(resultSet);
                logger.traceExit("Found the participant by the fields: " + participant);
                return Optional.of(_participant);
            }
        } catch (SQLException e) {
            logger.error("Couldn't find the participant by the fields: {} - {}", participant, e.getMessage());
            throw new RepositoryException("Couldn't find the participant by the fields: " + participant + " - " + e.getMessage());
        }

        logger.traceExit("Couldn't find the participant by the fields: " + participant);
        return Optional.empty();
    }

    @Override
    public Iterable<Participant> findParticipantsByTeam(String team) throws RepositoryException {
        logger.traceEntry("Trying to find the participants by the team: " + team);

        List<Participant> participants = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = this.preparedStatementMap.get("selectOnTeam");
            preparedStatement.setString(1, team);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Participant participant = this.extractFromResultSet(resultSet);
                participants.add(participant);
            }

            logger.traceExit("Found the participants by the team: " + team);
            return participants;
        } catch (SQLException e) {
            logger.error("Couldn't find the participants by the team: {} - {}", team, e.getMessage());
            throw new RepositoryException("Couldn't find the participants by the team: " + team + " - " + e.getMessage());
        }
    }
}
