package org.rhofman.jpa.unittest;

import org.rhofman.jpa.unittest.annotation.JpaUnitTest;
import org.rhofman.jpa.unittest.handler.CreationHandler;
import org.rhofman.jpa.unittest.handler.CreationHandlerFactory;
import org.apache.commons.io.IOUtils;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.StringTokenizer;

import static org.rhofman.jpa.unittest.JpaConstants.*;


abstract class JpaBasedTest {

    private CreationHandler creationHandler;

    private String jdbcUrl;
    private String jdbcDriver;

    private EntityManagerFactory entityManageFactory;
    private EntityManager entityManager;

    private Properties databaseProperties;
    private String unitName;

    private String datasetFile;
    private String createTablesFile;

    public JpaBasedTest() {
        setCreationHandler();
    }

    /**
     * Initialise the repository.
     * The repository must have a method setEntityManager().
     * <pre>
     *     e.g.
     *     protected void initialiseRepository() {
     *         this.bookRepo = new BookRepository()
     *         this.bookRepo.setEntityManager(this.{@link #getEntityManager()});
     *     }
     * </pre>
     *
     */
    public abstract void initialiseRepository();

    private void setCreationHandler() {
        JpaUnitTest jpaUnitTestAnnotation = this.getClass().getAnnotation(JpaUnitTest.class);
        if (jpaUnitTestAnnotation == null)
            throw new IllegalStateException(this.getClass().getSimpleName() + " must be annotated with @CreateTables.");
        creationHandler = CreationHandlerFactory.getHandler(jpaUnitTestAnnotation.strategy());
        jdbcUrl = jpaUnitTestAnnotation.jdbcUrl().isEmpty() ? DEFAULT_JDBC_URL : jpaUnitTestAnnotation.jdbcUrl();
        jdbcDriver = jpaUnitTestAnnotation.jdbcDriver().isEmpty() ? DEFAULT_JDBC_DRIVER : jpaUnitTestAnnotation.jdbcDriver();
        unitName = jpaUnitTestAnnotation.persistenceUnit().isEmpty() ? DEFAULT_UNIT_NAME : jpaUnitTestAnnotation.persistenceUnit();
        datasetFile = jpaUnitTestAnnotation.dataSetFile().isEmpty() ? null : jpaUnitTestAnnotation.dataSetFile();
        createTablesFile = jpaUnitTestAnnotation.createTablesFile().isEmpty() ? CREATE_TABLES_SQL_FILE : jpaUnitTestAnnotation.createTablesFile();
    }

    protected String getDatasetFile() {
        return datasetFile;
    }

    protected void createDatabaseProperties() {
        databaseProperties = JpaPropertiesProducer.createDBUrlProperties(jdbcUrl, jdbcDriver);
        creationHandler.addDatabaseProperties(databaseProperties);
    }

    protected void createDatabase() {
        try {
            entityManageFactory = Persistence.createEntityManagerFactory(unitName, databaseProperties);
            entityManager = entityManageFactory.createEntityManager();
        } catch (PersistenceException e) {
            throw new JpaUnitTestException("Peristence Unit " + unitName + " not found.", e);
        }
    }

    protected void beginTransaction() {
        if (!entityManager.getTransaction().isActive())
            entityManager.getTransaction().begin();
        else
            throw new IllegalStateException("There is already a transaction active.");
    }

    protected void createTables() {
        creationHandler.createTables(entityManager, createTablesFile);
    }

    protected EntityManagerFactory getEntityManageFactory() {
        return entityManageFactory;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Load data into the tables.
     * Implementation e.g. {@link SqlBasedTest}
     *
     * @throws JpaUnitTestException
     */
    abstract void loadData() throws JpaUnitTestException;

    protected void clearEntityManager() {
        entityManager.clear();
    }

    public static String getSql(String sqlSource) {
        try (InputStream resourceAsStream = JpaBasedTest.class.getClassLoader().getResourceAsStream(sqlSource)) {
            return IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeSql(EntityManager entityManager, String sql) throws JpaUnitTestException {
        StringTokenizer stringTokenizer = new StringTokenizer(sql, ";");
        while (stringTokenizer.hasMoreTokens()) {
            String sqlStatement = stringTokenizer.nextToken();
            if (!"".equals(sqlStatement.trim())) {
                try {
                    Query query = entityManager.createNativeQuery(sqlStatement);
                    query.executeUpdate();
                } catch (PersistenceException e) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    throw new JpaUnitTestException("Error while executing statement '" + sql +"'", e);
                }
            }
        }
    }

    protected abstract void closeTestDatabase();

    protected void rollback() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

    protected void commitTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        } else {
            throw new IllegalStateException("No transaction active.");
        }
    }

    protected void close() {
        entityManager.close();
        entityManageFactory.close();
    }

}
