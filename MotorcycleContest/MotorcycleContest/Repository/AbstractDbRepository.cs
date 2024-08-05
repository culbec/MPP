using System.Collections.ObjectModel;
using System.Data;
using log4net;
using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;
public abstract class AbstractDbRepository<TId, TE> : IRepository<TId, TE> where TE : Entity<TId>
{
    private static readonly ILog Log = LogManager.GetLogger("AbstractDbRepository");
    protected readonly IDictionary<string, string> Properties;

    protected AbstractDbRepository(IDictionary<string, string> properties)
    {
        Log.Info("Initializing the AbstractDbRepository...");
        Properties = properties;
    }

    public abstract TE? FindOne(TId tid);
    public abstract Collection<User>? FindAll();
    public abstract TE? Save(TE te);
    public abstract TE? Delete(TE te);
    public abstract TE? Update(TE te);

    protected abstract TE ExtractFromDataReader(IDataReader dataReader);
}