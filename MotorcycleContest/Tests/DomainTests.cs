using MotorcycleContest.Domain;

namespace Tests;

public class DomainTests
{
    [Test]
    public void EntityTests()
    {
        var entity = new Entity<int>(1);

        Assert.That(entity.Id, Is.EqualTo(1));
        Assert.That(entity.Id.GetType(), Is.EqualTo(typeof(int)));

        var entityCopy = new Entity<int>(entity.Id);

        Assert.That(entityCopy, Is.EqualTo(entity));
    }

    [Test]
    public void PersonTest()
    {
        var person = new Person<int>(1, "Andrew", "Garfield");

        Assert.Multiple(() =>
        {
            Assert.That(person.Id.GetType(), Is.EqualTo(typeof(int)));
            Assert.That(person.Id, Is.EqualTo(1));
            Assert.That(person.FirstName, Is.EqualTo("Andrew"));
            Assert.That(person.LastName, Is.EqualTo("Garfield"));
        });

        var personCopy = new Person<int>(person.Id, person.FirstName, person.LastName);

        Assert.That(personCopy, Is.EqualTo(person));
    }

    [Test]
    public void ParticipantTest()
    {
        var participant = new Participant(Guid.NewGuid(), "Andrew", "Garfield", "Suzuki", 1000);

        Assert.Multiple(() =>
        {
            Assert.That(participant.Id.GetType(), Is.EqualTo(typeof(Guid)));
            Assert.That(participant.FirstName, Is.EqualTo("Andrew"));
            Assert.That(participant.LastName, Is.EqualTo("Garfield"));
            Assert.That(participant.Team, Is.EqualTo("Suzuki"));
            Assert.That(participant.EngineCapacity, Is.EqualTo(1000));
        });

        var participantCopy =
            new Participant(participant.Id, participant.FirstName, participant.LastName, participant.Team, participant.EngineCapacity);

        Assert.That(participantCopy, Is.EqualTo(participant));
    }

    [Test]
    public void UserTest()
    {
        var user = new User(Guid.NewGuid(), "Andrew", "Garfield", "andrew.garfield1");

        Assert.Multiple(() =>
        {
            Assert.That(user.Id.GetType(), Is.EqualTo(typeof(Guid)));
            Assert.That(user.FirstName, Is.EqualTo("Andrew"));
            Assert.That(user.LastName, Is.EqualTo("Garfield"));
            Assert.That(user.Username, Is.EqualTo("andrew.garfield1"));
        });

        var userCopy = new User(user.Id, user.FirstName, user.LastName, user.Username);

        Assert.That(userCopy, Is.EqualTo(user));
    }

    [Test]
    public void RaceTest()
    {
        var race125 = new Race(Guid.NewGuid(), 125, 0);
        var race250 = new Race(Guid.NewGuid(), 250, 0);

        Assert.Multiple(() =>
        {
            Assert.That(race125.Id.GetType(), Is.EqualTo(typeof(Guid)));

            Assert.That(race125.EngineCapacity, Is.EqualTo(125));
            Assert.That(race250.EngineCapacity, Is.EqualTo(250));

            Assert.That(race125.NoParticipants, Is.EqualTo(0));

            Assert.That(race250.NoParticipants, Is.EqualTo(0));
        });

        var race125Copy = new Race(race125.Id, race125.EngineCapacity, race125.NoParticipants);
        Assert.That(race125Copy, Is.EqualTo(race125));
    }
}