namespace MotorcycleContest.Domain;

public class Participant(Guid id, string firstName, string lastName, string team, int engineCapacity)
    : Person<Guid>(id, firstName, lastName)
{
    public string Team => team;
    public int EngineCapacity => engineCapacity;

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(Team, ((Participant)obj).Team) &&
               Equals(EngineCapacity, ((Participant)obj).EngineCapacity);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(team) +
               EqualityComparer<int>.Default.GetHashCode(engineCapacity);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(team)}: {team}, {nameof(engineCapacity)}: {engineCapacity}";
    }
}