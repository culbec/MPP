namespace MotorcycleContest.Exceptions;

public class RepositoryException : ApplicationException
{
    public RepositoryException()
    {
    }

    public RepositoryException(string message) : base(message)
    {
    }

    public RepositoryException(string message, Exception cause) : base(message, cause)
    {
    }
}