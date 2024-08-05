namespace Model;

public class Race : Entity<int>
{
    public int EngineCapacity { get; set; }
    public int NoParticipants { get; set; }

    private Race()
    {
    }

    public new class Builder
    {
        private Race _member = new Race();

        public Builder Reset()
        {
            _member = new Race();
            return this;
        }

        public Builder SetId(int id)
        {
            _member.Id = id;
            return this;
        }

        public Builder SetEngineCapacity(int engineCapacity)
        {
            _member.EngineCapacity = engineCapacity;
            return this;
        }

        public Builder SetNoParticipants(int noParticipants)
        {
            _member.NoParticipants = noParticipants;
            return this;
        }

        public Race Build()
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
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(EngineCapacity, ((Race)obj).EngineCapacity) &&
               Equals(NoParticipants, ((Race)obj).NoParticipants);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<int>.Default.GetHashCode(EngineCapacity) +
               EqualityComparer<int>.Default.GetHashCode(NoParticipants);
    }

    public override string ToString()
    {
        return
            $"{base.ToString()}, {nameof(EngineCapacity)}: {EngineCapacity}, {nameof(NoParticipants)}: {NoParticipants}";
    }
}