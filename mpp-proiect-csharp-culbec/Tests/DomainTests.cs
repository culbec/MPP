using Model;

namespace Tests;

public class DomainTests
{
    [Test]
    public void EntityTests()
    {
        var entity = Entity<int>.NewBuilder()
            .SetId(1)
            .Build();

        Assert.That(entity.Id, Is.EqualTo(1));
        Assert.That(entity.Id.GetType(), Is.EqualTo(typeof(int)));

        var entityCopy = Entity<int>.NewBuilder()
            .SetId(entity.Id)
            .Build();

        Assert.That(entityCopy, Is.EqualTo(entity));
    }

    [Test]
    public void PersonTest()
    {
        var person = Person<int>.NewBuilder()
            .SetId(1)
            .SetFirstName("Andrew")
            .SetLastName("Garfield")
            .Build();

        Assert.Multiple(() =>
        {
            Assert.That(person.Id.GetType(), Is.EqualTo(typeof(int)));
            Assert.That(person.Id, Is.EqualTo(1));
            Assert.That(person.FirstName, Is.EqualTo("Andrew"));
            Assert.That(person.LastName, Is.EqualTo("Garfield"));
        });

        var personCopy = Person<int>.NewBuilder()
            .SetId(person.Id)
            .SetFirstName(person.FirstName!)
            .SetLastName(person.LastName!)
            .Build();

        Assert.That(personCopy, Is.EqualTo(person));
    }

    [Test]
    public void ParticipantTest()
    {
        var participant = Participant.NewBuilder()
            .SetId(Guid.NewGuid())
            .SetFirstName("Andrew")
            .SetLastName("Garfield")
            .SetTeam("Suzuki")
            .SetEngineCapacity(1000)
            .Build();

        Assert.Multiple(() =>
        {
            Assert.That(participant.Id.GetType(), Is.EqualTo(typeof(Guid)));
            Assert.That(participant.FirstName, Is.EqualTo("Andrew"));
            Assert.That(participant.LastName, Is.EqualTo("Garfield"));
            Assert.That(participant.Team, Is.EqualTo("Suzuki"));
            Assert.That(participant.EngineCapacity, Is.EqualTo(1000));
        });

        var participantCopy = Participant.NewBuilder()
            .SetId(participant.Id)
            .SetFirstName(participant.FirstName!)
            .SetLastName(participant.LastName!)
            .SetTeam(participant.Team!)
            .SetEngineCapacity(participant.EngineCapacity)
            .Build();

        Assert.That(participantCopy, Is.EqualTo(participant));
    }

    [Test]
    public void UserTest()
    {
        var user = User.NewBuilder()
            .SetId(1)
            .SetFirstName("Andrew")
            .SetLastName("Garfield")
            .SetUsername("andrew.garfield1")
            .Build();

        Assert.Multiple(() =>
        {
            Assert.That(user.Id.GetType(), Is.EqualTo(typeof(int)));
            Assert.That(user.FirstName, Is.EqualTo("Andrew"));
            Assert.That(user.LastName, Is.EqualTo("Garfield"));
            Assert.That(user.Username, Is.EqualTo("andrew.garfield1"));
        });

        var userCopy = User.NewBuilder()
            .SetId(user.Id)
            .SetFirstName(user.FirstName!)
            .SetLastName(user.LastName!)
            .SetUsername(user.Username!)
            .Build();

        Assert.That(userCopy, Is.EqualTo(user));
    }

    [Test]
    public void RaceTest()
    {
        var race125 = Race.NewBuilder()
            .SetId(1)
            .SetEngineCapacity(125)
            .SetNoParticipants(0)
            .Build();
        var race250 = Race.NewBuilder()
            .SetId(2)
            .SetEngineCapacity(250)
            .SetNoParticipants(0)
            .Build();

        Assert.Multiple(() =>
        {
            Assert.That(race125.Id.GetType(), Is.EqualTo(typeof(int)));

            Assert.That(race125.EngineCapacity, Is.EqualTo(125));
            Assert.That(race250.EngineCapacity, Is.EqualTo(250));

            Assert.That(race125.NoParticipants, Is.EqualTo(0));

            Assert.That(race250.NoParticipants, Is.EqualTo(0));
        });

        var race125Copy = Race.NewBuilder()
            .SetId(race125.Id)
            .SetEngineCapacity(race125.EngineCapacity)
            .SetNoParticipants(race125.NoParticipants)
            .Build();
        Assert.That(race125Copy, Is.EqualTo(race125));
    }
}