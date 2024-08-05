using Common.Exceptions;
using log4net;
using Model;
using Persistence.Repository;

namespace Server.Service;

public class ServiceUserImpl : IServiceUser
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ServiceUserImpl));
    private readonly IRepositoryUser _repositoryUser;

    public ServiceUserImpl(IRepositoryUser repositoryUser)
    {
        Log.Info("Initializing ServiceUserImpl.");
        _repositoryUser = repositoryUser;
        Log.Info("ServiceUserImpl initialized.");
    }

    public User Login(string username, string password)
    {
        Log.InfoFormat("Attempting to login user with username {0}.", username);

        try
        {
            var user = _repositoryUser.FindUserByCredentials(username, password);

            if (user == null)
            {
                Log.Error("The user couldn't be found.");
                throw new ServiceException("The user couldn't be found.");
            }

            Log.Info("User logged in successfully.");
            return user;
        }
        catch (RepositoryException e)
        {
            Log.ErrorFormat("Something went wrong when trying to login user with username {0}: {1}", username,
                e.Message);
            throw new ServiceException("Something went wrong when trying to login user with username " + username +
                                       ": " + e.Message);
        }
    }
}