using System.ComponentModel;
using System.Configuration;
using Microsoft.VisualStudio.TestPlatform.ObjectModel.Adapter;
using Model;
using Persistence.ConnectionUtils;
using Persistence.Repository;

namespace Tests;

public class RepositoryTests
{
    private static readonly RepositoryUserDb RepositoryUser;
    private static readonly RepositoryParticipantDb RepositoryParticipant;
    private static readonly SortedList<string, string> Properties = new();

    static RepositoryTests()
    {
        var connectionString = GetConnectionStringByName("MariaDBConnectionString");
        if (connectionString == null) throw new TestCanceledException("Cannot get configuration for the DB!");

        Console.WriteLine("ConnectionString for DB {0}", connectionString);
        Properties.Add("ConnectionString", connectionString);

        RepositoryUser = new RepositoryUserDb(Properties);
        RepositoryParticipant = new RepositoryParticipantDb(Properties);
    }

    [SetUp]
    [DisplayName("Repository Tests Setup")]
    public void Setup()
    {
        AddUsers(Properties);
    }

    [TearDown]
    [DisplayName("Repository Test Teardown")]
    public void TearDown()
    {
        RemoveUsers(Properties);
        RemoveParticipants(Properties);
    }

    [Test]
    [DisplayName("Repository Tests - User Repository Test")]
    public void UserRepositoryTest()
    {
        var test1UserValid = RepositoryUser.FindUserByCredentials("test1", "1234");
        Assert.That(test1UserValid, Is.Not.Null);
        Assert.Multiple(() =>
        {
            Assert.That(test1UserValid!.FirstName, Is.EqualTo("Test1F"));
            Assert.That(test1UserValid.LastName, Is.EqualTo("Test1L"));
            Assert.That(test1UserValid.Username, Is.EqualTo("test1"));
        });

        var test1UserInvalid = RepositoryUser.FindUserByCredentials("test1", "12345");
        Assert.That(test1UserInvalid, Is.Null);

        var test2UserValid = RepositoryUser.FindUserByCredentials("test2", "suzuki_power");
        Assert.That(test2UserValid, Is.Not.Null);
        Assert.Multiple(() =>
        {
            Assert.That(test2UserValid!.FirstName, Is.EqualTo("Test2F"));
            Assert.That(test2UserValid.LastName, Is.EqualTo("Test2L"));
            Assert.That(test2UserValid.Username, Is.EqualTo("test2"));
        });
    }

    [Test]
    [DisplayName("Repository Tests - Participant Repository Test")]
    public void ParticipantRepositoryTest()
    {
        var participant1 = Participant.NewBuilder()
            .SetId(Guid.NewGuid())
            .SetFirstName("Test1F")
            .SetLastName("Test1L")
            .SetTeam("Test1T")
            .SetEngineCapacity(1000)
            .Build();
        var participant2 = Participant.NewBuilder()
            .SetId(Guid.NewGuid())
            .SetFirstName("Test2F")
            .SetLastName("Test2L")
            .SetTeam("Test2T")
            .SetEngineCapacity(2000)
            .Build();

        var participant1Saved = RepositoryParticipant.Save(participant1);
        Assert.That(participant1Saved, Is.Null);

        var participant2Saved = RepositoryParticipant.Save(participant2);
        Assert.That(participant2Saved, Is.Null);

        participant1Saved = RepositoryParticipant.Save(participant1);
        Assert.That(participant1Saved, Is.Not.Null);

        var participant1ByFields = RepositoryParticipant.FindParticipantByFields(participant1);
        Assert.That(participant1ByFields, Is.EqualTo(participant1));

        var participantsByTeam = RepositoryParticipant.FindParticipantsByTeam("Test1T");
        Assert.That(participantsByTeam.Count(), Is.EqualTo(1));
    }

    private static void AddUsers(IDictionary<string, string> properties)
    {
        var test1Pass = BCrypt.Net.BCrypt.HashPassword("1234");
        var test2Pass = BCrypt.Net.BCrypt.HashPassword("suzuki_power");

        using var connection = DBUtils.GetConnection(properties);
        using var command = connection.CreateCommand();
        command.CommandText =
            "insert into users(first_name, last_name, username, password) values " +
            "(@firstName1, @lastName1, @username1, @password1), " +
            "(@firstName2, @lastName2, @username2, @password2)";

        var paramFirstName1 = command.CreateParameter();
        paramFirstName1.ParameterName = "@firstName1";
        paramFirstName1.Value = "Test1F";
        command.Parameters.Add(paramFirstName1);

        var paramLastName1 = command.CreateParameter();
        paramLastName1.ParameterName = "@lastName1";
        paramLastName1.Value = "Test1L";
        command.Parameters.Add(paramLastName1);

        var paramUsername1 = command.CreateParameter();
        paramUsername1.ParameterName = "@username1";
        paramUsername1.Value = "test1";
        command.Parameters.Add(paramUsername1);

        var paramPassword1 = command.CreateParameter();
        paramPassword1.ParameterName = "@password1";
        paramPassword1.Value = test1Pass;
        command.Parameters.Add(paramPassword1);

        var paramFirstName2 = command.CreateParameter();
        paramFirstName2.ParameterName = "@firstName2";
        paramFirstName2.Value = "Test2F";
        command.Parameters.Add(paramFirstName2);

        var paramLastName2 = command.CreateParameter();
        paramLastName2.ParameterName = "@lastName2";
        paramLastName2.Value = "Test2L";
        command.Parameters.Add(paramLastName2);

        var paramUsername2 = command.CreateParameter();
        paramUsername2.ParameterName = "@username2";
        paramUsername2.Value = "test2";
        command.Parameters.Add(paramUsername2);

        var paramPassword2 = command.CreateParameter();
        paramPassword2.ParameterName = "@password2";
        paramPassword2.Value = test2Pass;
        command.Parameters.Add(paramPassword2);

        command.ExecuteNonQuery();
    }

    private static void RemoveUsers(IDictionary<string, string> properties)
    {
        using var connection = DBUtils.GetConnection(properties);
        using var command = connection.CreateCommand();

        command.CommandText = "delete from users where username = @username1 or username = @username2";

        var paramUsername1 = command.CreateParameter();
        paramUsername1.ParameterName = "@username1";
        paramUsername1.Value = "test1";
        command.Parameters.Add(paramUsername1);

        var paramUsername2 = command.CreateParameter();
        paramUsername2.ParameterName = "@username2";
        paramUsername2.Value = "test2";
        command.Parameters.Add(paramUsername2);

        command.ExecuteNonQuery();
    }

    private static void RemoveParticipants(IDictionary<string, string> properties)
    {
        using var connection = DBUtils.GetConnection(properties);
        using var command = connection.CreateCommand();

        command.CommandText = "delete from participants where first_name = @firstName1 or first_name = @firstName2";

        var paramFirstName1 = command.CreateParameter();
        paramFirstName1.ParameterName = "@firstName1";
        paramFirstName1.Value = "Test1F";
        command.Parameters.Add(paramFirstName1);

        var paramFirstName2 = command.CreateParameter();
        paramFirstName2.ParameterName = "@firstName2";
        paramFirstName2.Value = "Test2F";
        command.Parameters.Add(paramFirstName2);

        command.ExecuteNonQuery();
    }

    private static string? GetConnectionStringByName(string name)
    {
        var appConfigPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "app.config");
        var fileMap = new ExeConfigurationFileMap { ExeConfigFilename = appConfigPath };
        var config = ConfigurationManager.OpenMappedExeConfiguration(fileMap, ConfigurationUserLevel.None);
        var settings = config.ConnectionStrings.ConnectionStrings[name];
        return settings?.ConnectionString;
    }
}