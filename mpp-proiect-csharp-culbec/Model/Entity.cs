namespace Model;

public class Entity<TId>
{
    public virtual TId? Id { get; set; }

    internal Entity() {}

    public class Builder
    {
        private Entity<TId> _member = new();


        public Builder Reset()
        {
            _member = new Entity<TId>();
            return this;
        }

        public Builder SetId(TId id)
        {
            _member.Id = id;
            return this;
        }

        public Entity<TId> Build()
        {
            return _member;
        }
    }

    public static Builder NewBuilder()
    {
        return new Builder();
    }

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj)) return false;
        if (ReferenceEquals(this, obj)) return true;
        return obj.GetType() == GetType() && Equals(Id, ((Entity<TId>)obj).Id);
    }

    public override int GetHashCode()
    {
        return EqualityComparer<TId>.Default.GetHashCode(Id!);
    }

    public override string ToString()
    {
        return $"{nameof(Id)}: {Id}";
    }
}