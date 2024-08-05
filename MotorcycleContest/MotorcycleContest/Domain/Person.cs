namespace MotorcycleContest.Domain;

public class Person<TId>(TId id, string firstName, string lastName) : Entity<TId>(id)
{
    public string FirstName => firstName;
    public string LastName => lastName;

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(FirstName, ((Person<TId>)obj).FirstName) &&
               Equals(LastName, ((Person<TId>)obj).LastName);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(firstName) +
               EqualityComparer<string>.Default.GetHashCode(lastName);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(firstName)}: {firstName}, {nameof(lastName)}: {lastName}";
    }
}