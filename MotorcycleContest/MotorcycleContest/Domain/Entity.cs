namespace MotorcycleContest.Domain;

public class Entity<TId>(TId id)
{
    public TId Id => id;

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return obj.GetType() == GetType() && Equals(Id, ((Entity<TId>)obj).Id);
    }

    public override int GetHashCode()
    {
        return EqualityComparer<TId>.Default.GetHashCode(Id);
    }

    public override string ToString()
    {
        return $"{nameof(Id)}: {Id}";
    }
}