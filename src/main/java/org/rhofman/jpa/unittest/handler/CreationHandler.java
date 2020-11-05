package org.rhofman.jpa.unittest.handler;

import org.rhofman.jpa.unittest.JpaUnitTestException;

import javax.persistence.EntityManager;
import java.util.Properties;

public interface CreationHandler {
    void createTables(EntityManager entityManager, String createTablesFile) throws JpaUnitTestException;

    void addDatabaseProperties(Properties properties);
}
