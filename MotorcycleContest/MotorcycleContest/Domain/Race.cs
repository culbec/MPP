namespace MotorcycleContest.Domain;

public class Race(Guid id, int engineCapacity, int noParticipants) : Entity<Guid>(id)
{
    public int EngineCapacity => engineCapacity;
    public int NoParticipants => noParticipants;

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(EngineCapacity, ((Race)obj).EngineCapacity) &&
               Equals(NoParticipants, ((Race)obj).NoParticipants);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<int>.Default.GetHashCode(engineCapacity) +
               EqualityComparer<int>.Default.GetHashCode(NoParticipants);
    }

    public override string ToString()
    {
        return
            $"{base.ToString()}, {nameof(engineCapacity)}: {engineCapacity}, {nameof(NoParticipants)}: {NoParticipants}";
    }
}