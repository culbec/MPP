using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public interface IUserRepository : IRepository<Guid, User>
{
    /**
     * Find a user by its username and password.
     * @param username the username of the user to find.
     * @param password the password of the user to find.
     * @return the user with the given username and password or null if there is no user with the given username and password.
     */
    User? FindUserByCredentials(string username, string password);
}