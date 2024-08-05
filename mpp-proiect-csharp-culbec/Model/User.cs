using System.ComponentModel.DataAnnotations.Schema;

namespace Model;

using System.ComponentModel.DataAnnotations;

[Table("users")]
public class User : Person<int>
{
    [Key]
    [Required]
    [Column("uid")]
    public override int Id { get; set; }

    [Required]
    [StringLength(50)]
    [Column("first_name")]
    public override string? FirstName { get; set; }

    [Required]
    [StringLength(50)]
    [Column("last_name")]
    public override string? LastName { get; set; }

    [Required]
    [StringLength(30)]
    [Column("username")]
    public string? Username { get; set; }

    [Required]
    [StringLength(255)]
    [Column("password")]
    public string? Password { get; set; }

    public new class Builder
    {
        private User _member = new();

        public Builder Reset()
        {
            _member = new User();
            return this;
        }

        public Builder SetId(int id)
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

        public Builder SetUsername(string username)
        {
            _member.Username = username;
            return this;
        }

        public User Build()
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
        return base.Equals(obj) && obj.GetType() == GetType() && Equals(Username, ((User)obj).Username);
    }

    public override int GetHashCode()
    {
        return base.GetHashCode() + EqualityComparer<string>.Default.GetHashCode(Username!);
    }

    public override string ToString()
    {
        return $"{base.ToString()}, {nameof(Username)}: {Username}";
    }
}