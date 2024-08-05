using System.Collections.ObjectModel;
using System.Data;
using log4net;
using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public class UserDbRepository : AbstractDbRepository<Guid, User>, IUserRepository
{
    private static readonly ILog Log = LogManager.GetLogger("UserDbRepository");

    public UserDbRepository(IDictionary<string, string> properties) : base(properties)
    {
        Log.Info("Initializing the UserDbRepository...");
    }

    public override User? FindOne(Guid tid)
    {
        return null;
    }

    public override Collection<User>? FindAll()
    {
        return null;
    }

    public override User? Save(User te)
    {
        return null;
    }

    public override User? Delete(User te)
    {
        return null;
    }

    public override User? Update(User te)
    {
        return null;
    }

    public User? FindUserByCredentials(string username, string password)
    {
        Log.InfoFormat("Finding user with username {0} and password {1}", username, password);

        Log.Info("Establishing a connection with the database...");
        using (var connection = DBUtils.GetConnection(Properties))
        {
            Log.Info("Connection established.");
            Log.Info("Creating a command...");
            using (var command = connection.CreateCommand())
            {
                Log.Info("Command created.");
                command.CommandText = "SELECT uid, first_name, last_name, username, password FROM users WHERE username = @username";

                Log.Info("Populating the command with parameters...");
                var usernameParam = command.CreateParameter();
                usernameParam.ParameterName = "@username";
                usernameParam.Value = username;
                command.Parameters.Add(usernameParam);

                Log.Info("Executing the command...");
                using (var dataReader = command.ExecuteReader())
                {
                    Log.Info("Command executed.");

                    if (dataReader.Read())
                    {
                        Log.Info("User found.");
                        Log.Info("Checking the password...");
                        var hashedPassword = dataReader.GetString(4);

                        if (!CheckPassword(password, hashedPassword))
                        {
                            Log.Error("Password is incorrect.");
                            return null;
                        }

                        Log.Info("Password is correct. Initializing the user.");

                        var id = dataReader.GetGuid(0);
                        var firstName = dataReader.GetString(1);
                        var lastName = dataReader.GetString(2);

                        var user = new User(id, firstName, lastName, username);

                        Log.InfoFormat("User initialized. Returning with {0}", user);
                        return user;
                    }
                }
            }
        }

        Log.InfoFormat("No user found with username {0} and password {1}.", username, password);
        return null;
    }


    protected override User ExtractFromDataReader(IDataReader dataReader)
    {
        Log.Info("Extracting user from data reader...");
        var id = dataReader.GetGuid(0);
        var firstName = dataReader.GetString(1);
        var lastName = dataReader.GetString(2);
        var username = dataReader.GetString(3);

        Log.Info("User extracted.");
        return new User(id, firstName, lastName, username);
    }

    private static bool CheckPassword(string password, string hashedPassword)
    {
        Log.InfoFormat("Checking password {0} against hashed password {1}", password, hashedPassword);
        return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
    }
}