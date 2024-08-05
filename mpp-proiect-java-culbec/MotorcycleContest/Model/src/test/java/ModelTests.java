import Model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTests {
    @Test
    @DisplayName("Model.Entity test - Domain Test")
    public void EntityTest() {
        Entity<Integer> entityInteger = new Entity.Builder<Integer>()
                .setId(1)
                .build();
        Entity<String> entityString = new Entity.Builder<String>()
                .setId("1")
                .build();
        Entity<Double> entityDouble = new Entity.Builder<Double>()
                .setId(1.0)
                .build();

        assertEquals(Integer.class, entityInteger.getId().getClass());
        assertEquals(1, entityInteger.getId(), "This object's ID should be an integer with the value 1.");

        assertEquals(String.class, entityString.getId().getClass());
        assertEquals("1", entityString.getId(), "This object's ID should be a string with the value '1'.");

        assertEquals(Double.class, entityDouble.getId().getClass());
        assertEquals(1.0, entityDouble.getId(), "This object's ID should be a double with the value 1.0.");

        Entity<Integer> entityIntegerCopy = new Entity.Builder<Integer>()
                .setId(entityInteger.getId())
                .build();

        assertEquals(entityInteger, entityIntegerCopy, "The two entities should be equal.");
    }

    @Test
    @DisplayName("Model.Person test - Domain Test")
    public void PersonTest() {
        Person<Integer> person1 = new Person.Builder<Integer>()
                .setId(1)
                .setFirstName("Andrew")
                .setLastName("Garfield")
                .build();
        Person<Integer> person2 = new Person.Builder<Integer>()
                .setId(2)
                .setFirstName("Andrew")
                .setLastName("Garfield")
                .build();
        Person<Integer> person3 = new Person.Builder<Integer>()
                .setId(3)
                .setFirstName("Andrew")
                .setLastName("Garfield")
                .build();

        assertEquals("Andrew", person1.getFirstName(), "The first person's first name should be 'Andrew'.");
        assertEquals("Garfield", person1.getLastName(), "The first person's last name should be 'Garfield'.");

        assertEquals(2, person2.getId(), "The ids should be equal.");

        assertEquals(Integer.class, person3.getId().getClass(), "The third person's ID should be an integer.");

        Person<Integer> person1Copy = new Person.Builder<Integer>()
                .setId(person1.getId())
                .setFirstName(person1.getFirstName())
                .setLastName(person1.getLastName())
                .build();
        assertEquals(person1, person1Copy, "The two persons should be equal.");
    }

    @Test
    @DisplayName("Model.Participant test - Domain Test")
    public void ParticipantTest() {
        Participant participant = new Participant.Builder()
                .setId(UUID.randomUUID())
                .setFirstName("Andrew")
                .setLastName("Garfield")
                .setTeam("Suzuki")
                .setEngineCapacity(125)
                .build();

        assertEquals(UUID.class, participant.getId().getClass(), "This participant's ID should be a UUID.");

        assertEquals("Andrew", participant.getFirstName(), "This participant's first name should be 'Andrew'.");
        assertEquals("Garfield", participant.getLastName(), "This participant's last name should be 'Garfield'.");

        assertEquals("Suzuki", participant.getTeam(), "This participant's team should be 'Suzuki'.");
        assertEquals(125, participant.getEngineCapacity(), "This participant's engine capacity should be '125'.");

        Participant participantCopy = new Participant.Builder()
                .setId(participant.getId())
                .setFirstName(participant.getFirstName())
                .setLastName(participant.getLastName())
                .setTeam(participant.getTeam())
                .setEngineCapacity(participant.getEngineCapacity())
                .build();
        assertEquals(participant, participantCopy, "The two participants should be equal.");
    }

    @Test
    @DisplayName("Model.User test - Domain Test")
    public void UserTest() {
        User user = new User.Builder()
                .setId(2)
                .setFirstName("Andrew")
                .setLastName("Garfield")
                .setUsername("andrew.garfield1")
                .build();

        assertEquals(Integer.class, user.getId().getClass(), "This user's ID should be a UUID.");

        assertEquals("Andrew", user.getFirstName(), "This user's first name should be 'Andrew'.");
        assertEquals("Garfield", user.getLastName(), "This user's last name should be 'Garfield'.");

        assertEquals("andrew.garfield1", user.getUsername(), "This user's username should be 'andrew.garfield1'.");

        User userCopy = new User.Builder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setUsername(user.getUsername())
                .build();

        assertEquals(user, userCopy, "The two users should be equal.");
    }

    @Test
    @DisplayName("Model.Race test - Domain Test")
    public void RaceTest() {
        Race race125 = new Race.Builder()
                .setId(1)
                .setNoParticipants(10)
                .setEngineCapacity(125)
                .build();
        Race race250 = new Race.Builder()
                .setId(2)
                .setNoParticipants(20)
                .setEngineCapacity(250)
                .build();

        assertEquals(race125.getEngineCapacity(), 125, "The first race's engine capacity should be 125.");
        assertEquals(race250.getEngineCapacity(), 250, "The first race's engine capacity should be 250.");

        assertEquals(race125.getNoParticipants(), 10, "The 125mc race should have 10 participants.");

        assertEquals(race250.getNoParticipants(), 20, "The 250mc race should have 20 participants.");

        Race race125Copy = new Race.Builder()
                .setId(race125.getId())
                .setNoParticipants(race125.getNoParticipants())
                .setEngineCapacity(race125.getEngineCapacity())
                .build();
        assertEquals(race125Copy, race125, "The two races should be equal.");
    }
}
