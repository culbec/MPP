using System.ComponentModel;
using System.Configuration;
using log4net.Config;
using Microsoft.VisualStudio.TestPlatform.ObjectModel.Adapter;
using MotorcycleContest;
using MotorcycleContest.Domain;
using MotorcycleContest.Repository;

namespace Tests;

public class RepositoryTests
{
    private static readonly UserDbRepository UserRepository;
    private static readonly ParticipantDbRepository ParticipantRepository;
    private static readonly SortedList<string, string> Properties = new();

    static RepositoryTests()
    {
        var connectionString = GetConnectionStringByName("MariaDBConnectionString");
        if (connectionString == null)
        {
            throw new TestCanceledException("Cannot get configuration for the DB!");
        }

        Console.WriteLine("ConnectionString for DB {0}", connectionString);
        Properties.Add("ConnectionString", connectionString);

        UserRepository = new UserDbRepository(Properties);
        ParticipantRepository = new ParticipantDbRepository(Properties);
    }

    [SetUp]
    [DisplayName(displayName: "Repository Tests Setup")]
    public void Setup()
    {
        AddUsers(Properties);
    }

    [TearDown]
    [DisplayName(displayName: "Repository Test Teardown")]
    public void TearDown()
    {
        RemoveUsers(Properties);
        RemoveParticipants(Properties);
    }

    [Test]
    [DisplayName(displayName: "Repository Tests - User Repository Test")]
    public void UserRepositoryTest()
    {
        var test1UserValid = UserRepository.FindUserByCredentials("test1", "1234");
        Assert.That(test1UserValid, Is.Not.Null);
        Assert.Multiple(() =>
        {
            Assert.That(test1UserValid!.FirstName, Is.EqualTo("Test1F"));
            Assert.That(test1UserValid.LastName, Is.EqualTo("Test1L"));
            Assert.That(test1UserValid.Username, Is.EqualTo("test1"));
        });

        var test1UserInvalid = UserRepository.FindUserByCredentials("test1", "12345");
        Assert.That(test1UserInvalid, Is.Null);

        var test2UserValid = UserRepository.FindUserByCredentials("test2", "suzuki_power");
        Assert.That(test2UserValid, Is.Not.Null);
        Assert.That(test2UserValid!.FirstName, Is.EqualTo("Test2F"));
        Assert.That(test2UserValid.LastName, Is.EqualTo("Test2L"));
        Assert.That(test2UserValid.Username, Is.EqualTo("test2"));
    }

    [Test]
    [DisplayName(displayName: "Repository Tests - Participant Repository Test")]
    public void ParticipantRepositoryTest()
    {
        var participant1 = new Participant(Guid.NewGuid(), "Test1F", "Test2L", "Test1T", 1000);
        var participant2 = new Participant(Guid.NewGuid(), "Test2F", "Test2L", "Test2T", 2000);

        var participant1Saved = ParticipantRepository.Save(participant1);
        Assert.That(participant1Saved, Is.Null);

        var participant2Saved = ParticipantRepository.Save(participant2);
        Assert.That(participant2Saved, Is.Null);

        participant1Saved = ParticipantRepository.Save(participant1);
        Assert.That(participant1Saved, Is.Not.Null);

        var participant1ByFields = ParticipantRepository.FindParticipantByFields(participant1);
        Assert.That(participant1ByFields, Is.EqualTo(participant1));

        var participantsByTeam = ParticipantRepository.FindParticipantsByTeam("Test1T");
        Assert.That(participantsByTeam!.Count(), Is.EqualTo(1));
    }

    private static void AddUsers(IDictionary<string, string> properties)
    {
        var test1Pass = BCrypt.Net.BCrypt.HashPassword("1234");
        var test2Pass = BCrypt.Net.BCrypt.HashPassword("suzuki_power");

        using var connection = DBUtils.GetConnection(properties);
        using var command = connection.CreateCommand();
        command.CommandText =
            "insert into users(uid, first_name, last_name, username, password) values " +
            "(@uid1, @firstName1, @lastName1, @username1, @password1), " +
            "(@uid2, @firstName2, @lastName2, @username2, @password2)";

        var paramUid1 = command.CreateParameter();
        paramUid1.ParameterName = "@uid1";
        paramUid1.Value = Guid.NewGuid();
        command.Parameters.Add(paramUid1);

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

        var paramUid2 = command.CreateParameter();
        paramUid2.ParameterName = "@uid2";
        paramUid2.Value = Guid.NewGuid();
        command.Parameters.Add(paramUid2);

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