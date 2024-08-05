using Microsoft.EntityFrameworkCore;
using Model;
using Persistence.ConnectionUtils;

namespace Persistence.Repository;

public class RepositoryUserEfCore : AbstractEfCoreRepository<int, User>, IRepositoryUser
{
    public RepositoryUserEfCore(IDictionary<string, string> properties) : base(properties)
    {
    }

    public override User? FindOne(int tid)
    {
        return null;
    }

    public override IEnumerable<User>? FindAll()
    {
        Log.Info("Retrieving all users...");

        using var context = EFCoreDBUtils.GetContext(Properties);
        var users = context.Users.ToList();

        if (users.Count != 0)
        {
            Log.InfoFormat("{0} users retrieved.", users.Count);
            return users;
        }

        Log.Info("No users found.");
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
        Log.InfoFormat("Finding user with username {0}", username);
        var context = EFCoreDBUtils.GetContext(Properties);
        var user = context.Users.FirstOrDefault(u => u.Username == username);
        if (user is not null)
        {
            if (!CheckPassword(password, user.Password!))
            {
                Log.Error("The password is incorrect.");
                return null;
            }

            Log.InfoFormat("The user was found: {0}", user);
            return user;
        }

        Log.Error("The user was not found.");
        return null;
    }

    private bool CheckPassword(string password, string hashedPassword) => BCrypt.Net.BCrypt.Verify(password, hashedPassword);
}