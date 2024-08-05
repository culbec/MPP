using Model;

namespace Persistence.Repository;

public interface IRepositoryUser : IRepository<int, User>
{
    /// <summary>
    ///     Find a user by its username and password.
    /// </summary>
    /// <param name="username">The username of the user to find</param>
    /// <param name="password">The password of the user to find</param>
    /// <returns>
    ///     The user with the given username and password or null if there is no user with the given username and
    ///     password.
    /// </returns>
    User? FindUserByCredentials(string username, string password);
}