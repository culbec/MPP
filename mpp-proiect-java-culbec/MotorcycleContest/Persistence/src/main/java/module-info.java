module MotorcycleContest.Persistence.main {
    exports Repository;
    exports ConnectionUtils;
    requires MotorcycleContest.Model.main;
    requires MotorcycleContest.Common.main;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires spring.security.crypto;
    requires org.hibernate.orm.core;
    requires spring.context;
    requires spring.beans;
    requires java.naming;
    requires jakarta.persistence;
}