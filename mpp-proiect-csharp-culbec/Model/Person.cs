using Model;

namespace Model;

public class Person<TId> : Entity<TId>
{
    public virtual string? FirstName { get; set; }

    public virtual string? LastName { get; set; }

    internal Person()
    {

    }

    public new class Builder
    {
        private Person<TId> _member = new Person<TId>();

        public Builder Reset()
        {
            _member = new Person<TId>();
            return this;
        }

        public Builder SetId(TId id)
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

        public Person<TId> Build()
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
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(FirstName, ((Person<TId>)obj).FirstName) &&
               Equals(LastName, ((Person<TId>)obj).LastName);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(FirstName!) +
               EqualityComparer<string>.Default.GetHashCode(LastName!);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(FirstName)}: {FirstName}, {nameof(LastName)}: {LastName}";
    }
}