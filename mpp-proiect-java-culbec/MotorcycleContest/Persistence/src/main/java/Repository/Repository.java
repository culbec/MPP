package Repository;

import java.util.Optional;

public interface Repository<ID, E> {
    /**
     * @param id - the id of the entity to be found.
     * @return an {@code Optional} encapsulating the entity with the given id if it exists, or {@code Optional.empty()} otherwise.
     */
    Optional<E> findOne(ID id) throws RepositoryException;

    /**
     * @return a {@code Collection} containing all the entities.
     */
    Iterable<E> findAll() throws RepositoryException;

    /**
     * @param e - the entity to be saved.
     * @return an {@code Optional} encapsulating the entity with the given id if it was saved, or {@code Optional.empty()} otherwise.
     */
    Optional<E> save(E e) throws RepositoryException;

    /**
     * @param e - the entity to be deleted.
     * @return an {@code Optional} encapsulating the entity with the given id if it was deleted, or {@code Optional.empty()} otherwise.
     */
    Optional<E> delete(E e);

    /**
     * @param e - the entity to be updated.
     * @return an {@code Optional} encapsulating the entity with the given id if it was updated, or {@code Optional.empty()} otherwise.
     */
    Optional<E> update(E e) throws RepositoryException;


}
