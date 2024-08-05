using Model;

namespace Persistence.Repository;

public interface IRepository<in TId, TE> where TE : Entity<TId>
{
    TE? FindOne(TId tid);
    IEnumerable<TE>? FindAll();
    TE? Save(TE te);
    TE? Delete(TE te);
    TE? Update(TE te);
}