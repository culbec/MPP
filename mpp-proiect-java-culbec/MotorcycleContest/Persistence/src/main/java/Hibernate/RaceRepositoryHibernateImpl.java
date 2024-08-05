package Hibernate;

import Model.RaceORM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RaceRepositoryHibernateImpl implements RaceRepositoryHibernate {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public RaceRepositoryHibernateImpl() {
    }

    @Override
    public Optional<RaceORM> findOne(Integer integer) {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            logger.info("Retrieving the race with id: {}", integer);
            return  session.createSelectionQuery("from RaceORM where id = :id", RaceORM.class)
                    .setParameter("id", integer)
                    .uniqueResultOptional();
        }
    }

    @Override
    public Iterable<RaceORM> findAll() {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            logger.info("Retrieving all the races...");
            return session.createSelectionQuery("from RaceORM", RaceORM.class).getResultList();
        }
    }

    @Override
    public Optional<RaceORM> save(RaceORM raceORM) {
        logger.info("Saving the race: {}", raceORM);
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(raceORM));
        return Optional.of(raceORM);
    }

    @Override
    public Optional<RaceORM> delete(RaceORM raceORM) {
        return Optional.empty();
    }

    @Override
    public Optional<RaceORM> update(RaceORM raceORM) {
        AtomicReference<Optional<RaceORM>> old = new AtomicReference<>(Optional.empty());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            RaceORM oldRaceORM = session.get(RaceORM.class, raceORM.getId());
            if (!Objects.isNull(oldRaceORM)) {
                logger.info("Updating the race with id: {}", raceORM.getId());
                session.merge(raceORM);
                session.flush();
                old.set(Optional.of(oldRaceORM));
            } else {
                logger.warn("Couldn't find the race with id: {}", raceORM.getId());
                old.set(Optional.empty());
            }
        });

        return old.get();
    }

    @Override
    public Optional<RaceORM> delete(Integer integer) {
        AtomicReference<Optional<RaceORM>> old = new AtomicReference<>(Optional.empty());
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            RaceORM oldRaceORM = session.get(RaceORM.class, integer);
            if (!Objects.isNull(oldRaceORM)) {
                logger.info("Deleting the race with id: {}", integer);
                session.remove(oldRaceORM);
                session.flush();
                old.set(Optional.of(oldRaceORM));
            } else {
                logger.warn("Couldn't find the race with id: {}", integer);
            }
        });

        return old.get();
    }
}
