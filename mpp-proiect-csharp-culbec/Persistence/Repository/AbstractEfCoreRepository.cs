using log4net;
using Model;

namespace Persistence.Repository;

public abstract class AbstractEfCoreRepository<TId, TE> : IRepository<TId, TE> where TE : Entity<TId>
{
    protected readonly ILog Log;
    protected readonly IDictionary<string, string> Properties;

    protected AbstractEfCoreRepository(IDictionary<string, string> properties)
    {
        Log = LogManager.GetLogger(GetType());

        Log.InfoFormat("Initializing the repository of type: {0}", GetType().Name);
        Properties = properties;
        Log.InfoFormat("Repository of type {0} initialized.", GetType().Name);
    }

    public abstract TE? FindOne(TId tid);

    public abstract IEnumerable<TE>? FindAll();

    public abstract TE? Save(TE te);

    public abstract TE? Delete(TE te);

    public abstract TE? Update(TE te);
}