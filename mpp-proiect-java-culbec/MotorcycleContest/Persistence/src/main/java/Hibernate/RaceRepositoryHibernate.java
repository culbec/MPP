package Hibernate;

import Model.RaceORM;
import Repository.Repository;

import java.util.Optional;

public interface RaceRepositoryHibernate extends Repository<Integer, RaceORM> {
    Optional<RaceORM> delete(Integer integer);
}
