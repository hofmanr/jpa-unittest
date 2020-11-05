package org.rhofman.jpa.unittest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class SqlBasedTest extends JpaBasedTest {

    @BeforeEach
    public void setupDBTest() {
        createDatabaseProperties();
        createDatabase();
        initialiseRepository();

        beginTransaction();
        createTables();
        loadData();
        commitTransaction();

        clearEntityManager();
        beginTransaction();
    }

    @AfterEach
    public void teardownDBTest() {
        rollback();
        close();
    }

    @Override
    void loadData() throws JpaUnitTestException {
        if (getDatasetFile() != null) {
            executeSql(getEntityManager(), getSql(getDatasetFile()));
        }
    }
}
