namespace Persistence.Repository;

public class RepositoryException : Exception
{
    public RepositoryException() {}
    public RepositoryException(string message) : base(message) {}
}