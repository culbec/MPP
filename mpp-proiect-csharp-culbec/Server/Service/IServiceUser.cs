

using Common.CommonUtils;
using Common.Exceptions;
using Model;

namespace Server.Service;

public interface IServiceUser
{
    /// <summary>
    ///     Logins a user into the app.
    /// </summary>
    /// <param name="username">Username of the user.</param>
    /// <param name="password">Password of the user.</param>
    /// <returns>The logged in user, if it exists.</returns>
    /// <exception cref="ServiceException">If the username couldn't be found or couldn't be retrieved.</exception>
    User Login(string username, string password);
}