import ConnectionUtils.DBUtils;
import Model.Participant;
import Model.User;
import Repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryTests {
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final PreparedStatement insertUser;
    private final PreparedStatement deleteUser;
    private final PreparedStatement deleteParticipant;

    public RepositoryTests() throws RepositoryException, SQLException {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("DB.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DBUtils dbUtils = new DBUtils(properties);
        this.userRepository = new UserDBRepository(dbUtils);
        this.participantRepository = new ParticipantDBRepository(dbUtils);

        Connection connection = dbUtils.getConnection();
        this.insertUser = connection.prepareStatement("insert into users(uid, first_name, last_name, username, password) values(?, ?, ?, ?, ?)");
        this.deleteUser = connection.prepareStatement("delete from users where username = ?");

        this.deleteParticipant = connection.prepareStatement("delete from participants where first_name = ?");
    }

    private void beforeUser() throws SQLException {
        String test1Pass = BCrypt.hashpw("1234", BCrypt.gensalt());
        String test2Pass = BCrypt.hashpw("suzuki_power", BCrypt.gensalt());

        insertUser.setString(1, UUID.randomUUID().toString());
        insertUser.setString(2, "Test1F");
        insertUser.setString(3, "Test1L");
        insertUser.setString(4, "test1");
        insertUser.setString(5, test1Pass);

        insertUser.executeUpdate();

        insertUser.setString(1, UUID.randomUUID().toString());
        insertUser.setString(2, "Test2F");
        insertUser.setString(3, "Test2L");
        insertUser.setString(4, "test2");
        insertUser.setString(5, test2Pass);

        insertUser.executeUpdate();
    }

    private void afterUser() throws SQLException {
        deleteUser.setString(1, "test1");
        deleteUser.executeUpdate();

        deleteUser.setString(1, "test2");
        deleteUser.executeUpdate();
    }

    @Test
    @DisplayName("Model.User Repository - Repository Test")
    public void UserRepositoryTest() throws SQLException, RepositoryException {
        this.beforeUser();

        Optional<User> test1UserOptionalValid = userRepository.findUserByCredentials("test1", "1234");
        assertTrue(test1UserOptionalValid.isPresent(), "The user should be present.");
        assertEquals("Test1F", test1UserOptionalValid.get().getFirstName(), "The first name should be 'Test1F'.");
        assertEquals("Test1L", test1UserOptionalValid.get().getLastName(), "The last name should be 'Test1L'.");
        assertEquals("test1", test1UserOptionalValid.get().getUsername(), "The username should be 'test1'.");

        Optional<User> test1UserOptionalInvalid = userRepository.findUserByCredentials("test1", "12345");
        assertTrue(test1UserOptionalInvalid.isEmpty(), "The user should not be present.");

        Optional<User> test2UserOptionalValid = userRepository.findUserByCredentials("test2", "suzuki_power");
        assertTrue(test2UserOptionalValid.isPresent(), "The user should be present.");
        assertEquals("Test2F", test2UserOptionalValid.get().getFirstName(), "The first name should be 'Test2F'.");
        assertEquals("Test2L", test2UserOptionalValid.get().getLastName(), "The last name should be 'Test2L'.");
        assertEquals("test2", test2UserOptionalValid.get().getUsername(), "The username should be 'test2'.");

        this.afterUser();
    }

    private void afterParticipant() throws SQLException {
        deleteParticipant.setString(1, "Test1F");
        deleteParticipant.executeUpdate();

        deleteParticipant.setString(1, "Test2F");
        deleteParticipant.executeUpdate();
    }

    @Test
    @DisplayName("Model.Participant Repository - Repository Test")
    public void ParticipantRepositoryTest() throws SQLException, RepositoryException {
        Participant participant1 = new Participant.Builder()
                .setId(UUID.randomUUID())
                .setFirstName("Test1F")
                .setLastName("Test1L")
                .setTeam("Test1T")
                .setEngineCapacity(1000)
                .build();
        Participant participant2 = new Participant.Builder()
                .setId(UUID.randomUUID())
                .setFirstName("Test2F")
                .setLastName("Test2L")
                .setTeam("Test2T")
                .setEngineCapacity(2000)
                .build();

        Optional<Participant> participantOptional1 = participantRepository.save(participant1);
        assertTrue(participantOptional1.isPresent(), "The participant should not be present.");

        Optional<Participant> participantOptional2 = participantRepository.save(participant2);
        assertTrue(participantOptional2.isPresent(), "The participant should not be present.");

        Optional<Participant> existingParticipant = participantRepository.findParticipantByFields(participant1);
        assertTrue(existingParticipant.isPresent(), "The participant should be present.");

        Optional<Participant> participantByFields = participantRepository.findParticipantByFields(participant1);
        assertTrue(participantByFields.isPresent(), "The participant should be present.");
        assertEquals(participant1, participantByFields.get(), "The participant should be the same.");

        List<Participant> participantsByTeam = (List<Participant>) participantRepository.findParticipantsByTeam("Test1T");
        assertEquals(1, participantsByTeam.size(), "The number of participants should be 1.");

        this.afterParticipant();
    }
}
