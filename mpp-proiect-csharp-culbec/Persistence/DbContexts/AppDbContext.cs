using Microsoft.EntityFrameworkCore;
using Model;

namespace Persistence.DbContexts;

public class AppDbContext : DbContext
{
    public DbSet<User> Users { get; set; }
    public IDictionary<string, string>? Properties { get; init; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (Properties is null)
        {
            return;
        }
        var connectionString = Properties["ConnectionString"];
        optionsBuilder.UseMySql(connectionString, new MySqlServerVersion(new Version(8, 0, 21)));
    }
}