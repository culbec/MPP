namespace MotorcycleContest.Domain;

public class User(Guid id, string firstName, string lastName, string username) : Person<Guid>(id, firstName, lastName)
{
    public string Username => username;

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(Username, ((User)obj).Username);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(username);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(username)}: {username}";
    }
}