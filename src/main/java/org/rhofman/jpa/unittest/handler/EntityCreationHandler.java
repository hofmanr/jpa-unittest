package org.rhofman.jpa.unittest.handler;

import org.rhofman.jpa.unittest.JpaPropertiesProducer;
import org.rhofman.jpa.unittest.JpaUnitTestException;

import javax.persistence.EntityManager;
import java.util.Properties;

public class EntityCreationHandler implements CreationHandler {
    @Override
    public void createTables(EntityManager entityManager, String createTablesFile) throws JpaUnitTestException {
        // No implementation; this is handled by the JPA
        // See: JpaPropertiesProducer.addEntityCreationProperties()
    }

    @Override
    public void addDatabaseProperties(Properties properties) {
        properties.putAll(JpaPropertiesProducer.addEntityCreationProperties());
    }
}
