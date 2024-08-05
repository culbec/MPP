module Template.Persistence.main {
    exports Repository;

    requires org.apache.logging.log4j;
    requires spring.security.crypto;
    requires org.hibernate.orm.core;
    requires spring.context;
    requires spring.beans;
    requires java.naming;
    requires jakarta.persistence;

    requires Template.Common.main;
    requires Template.Model.main;

}