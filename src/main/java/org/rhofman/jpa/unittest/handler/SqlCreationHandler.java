package org.rhofman.jpa.unittest.handler;

import org.rhofman.jpa.unittest.JpaUnitTestException;
import org.rhofman.jpa.unittest.SqlBasedTest;

import javax.persistence.EntityManager;
import java.util.Properties;

public class SqlCreationHandler implements CreationHandler {

    @Override
    public void createTables(EntityManager entityManager, String createTablesFile) throws JpaUnitTestException {
        SqlBasedTest.executeSql(entityManager, SqlBasedTest.getSql(createTablesFile));
    }

    @Override
    public void addDatabaseProperties(Properties properties) {
        // No need for extra database properties
    }
}
