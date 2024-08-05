namespace Model;

public class Participant : Person<Guid>
{
    public string? Team { get; set; }
    public int EngineCapacity { get; set; }

    private Participant()
    {

    }

    public new class Builder
    {
        private Participant _member = new();

        public Builder Reset()
        {
            _member = new Participant();
            return this;
        }

        public Builder SetId(Guid id)
        {
            _member.Id = id;
            return this;
        }

        public Builder SetFirstName(string firstName)
        {
            _member.FirstName = firstName;
            return this;
        }

        public Builder SetLastName(string lastName)
        {
            _member.LastName = lastName;
            return this;
        }

        public Builder SetTeam(string team)
        {
            _member.Team = team;
            return this;
        }

        public Builder SetEngineCapacity(int engineCapacity)
        {
            _member.EngineCapacity = engineCapacity;
            return this;
        }

        public Participant Build()
        {
            return _member;
        }
    }

    public new static Builder NewBuilder()
    {
        return new Builder();
    }

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(Team, ((Participant)obj).Team) &&
               Equals(EngineCapacity, ((Participant)obj).EngineCapacity);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(Team!) +
               EqualityComparer<int>.Default.GetHashCode(EngineCapacity);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(Team)}: {Team}, {nameof(EngineCapacity)}: {EngineCapacity}";
    }
}