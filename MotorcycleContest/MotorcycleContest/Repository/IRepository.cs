using System.Collections.ObjectModel;
using MotorcycleContest.Domain;

namespace MotorcycleContest.Repository;

public interface IRepository<in TId, TE> where TE : Entity<TId>
{
    TE? FindOne(TId tid);
    Collection<User>? FindAll();
    TE? Save(TE te);
    TE? Delete(TE te);
    TE? Update(TE te);
}