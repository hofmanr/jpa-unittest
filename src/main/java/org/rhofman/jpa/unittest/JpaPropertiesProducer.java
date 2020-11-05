package org.rhofman.jpa.unittest;

import java.util.Properties;
import java.util.UUID;

import static org.rhofman.jpa.unittest.JpaConstants.*;

public class JpaPropertiesProducer {

    private JpaPropertiesProducer() {
    }

    /**
     * Create unique url for an in-memory Derby database
     * When there are more tests within a test-class, then every test (method) will have
     * it's own database.
     *
     * @param jdbcUrl
     * @param jdbcDriver
     * @return
     */
    public static Properties createDBUrlProperties(String jdbcUrl, String jdbcDriver) {
        final Properties properties = new Properties();
        final String uniqueName = UUID.randomUUID().toString();
        final String dbUrl = (jdbcUrl == null || jdbcUrl.isEmpty() ?
                DEFAULT_JDBC_URL + uniqueName + SUFFIX :
                jdbcUrl + uniqueName + SUFFIX);
        properties.put(URL_PROPERTY, dbUrl);
        if (jdbcDriver != null && !jdbcDriver.isEmpty())
            properties.put(DRIVER_PROPERTY, jdbcDriver);
        else
            properties.put(DRIVER_PROPERTY, DEFAULT_JDBC_DRIVER);
        return properties;
    }

    public static Properties addEntityCreationProperties() {
        Properties properties = new Properties();
        properties.put("javax.persistence.schema-generation.database.action", "create");
        properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        properties.put("javax.persistence.schema-generation.scripts.create-target", "./target/create_tables.sql");
        properties.put("javax.persistence.schema-generation.scripts.drop-target", "./target/drop_tables.sql");
        return properties;
    }
}
