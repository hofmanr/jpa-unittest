package org.rhofman.jpa.unittest;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.dbunit.database.IDatabaseConnection;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DBUnitBasedTest extends JpaBasedTest {

    private IDatabaseConnection connection;

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
    protected void createDatabase() {
        super.createDatabase();
        try {
            EntityManager em = getEntityManager();
            Connection sqlConnection;
            if ("org.eclipse.persistence.internal.jpa.EntityManagerImpl".equals(em.getClass().getName())) {
                em.getTransaction().begin();
                sqlConnection = em.unwrap(java.sql.Connection.class);
                connection = new DatabaseConnection(sqlConnection);
                em.getTransaction().commit();
                return;
            }

            if ("org.hibernate.internal.SessionImpl".equals(em.getClass().getName())) {
                Session session = em.unwrap(Session.class);
                session.doWork(c -> {
                    try {
                        connection = new DatabaseConnection(c);
                    } catch (DatabaseUnitException e) {
                        // ignore
                    }
                });
                return;
            }

            OpenJPAEntityManager oem = OpenJPAPersistence.cast(getEntityManager());
            sqlConnection = (Connection) oem.getConnection();
            connection = new DatabaseConnection(sqlConnection);
        } catch (DatabaseUnitException e) {
            throw new JpaUnitTestException("Error getting database connection for DBUnit: " + e.getMessage(), e);
        }
    }

    @Override
    void loadData() {
        if (getDatasetFile() == null) {
            throw new JpaUnitTestException("No data set present for DBUnit test!");
        }

        try {
            DatabaseOperation.CLEAN_INSERT.execute(connection, getXmlDataSet(getDatasetFile()));
        } catch (DatabaseUnitException e) {
            throw new JpaUnitTestException("Can not insert data set: " + e.getMessage(), e);
        } catch (SQLException throwables) {
            throw new JpaUnitTestException("SQL exception occurred.", throwables);
        }
    }

    private IDataSet getXmlDataSet(String dataSource) {
        try (InputStream dataSetStream = getClass().getClassLoader().getResourceAsStream(dataSource)) {
            FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
            return dataSetBuilder.build(dataSetStream);
        } catch (IOException | DataSetException e) {
            throw new JpaUnitTestException("Error loading data set " + dataSource, e);
        }
    }

    @Override
    protected void close() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throw new IllegalStateException("Can not close connection DBUnit.", throwables);
        }
        super.close();
    }
}
