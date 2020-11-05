package org.rhofman.jpa.unittest;

public class JpaConstants {

    public static final String CREATE_TABLES_SQL_FILE = "sql/create_tables.sql";

    public static final String DEFAULT_UNIT_NAME = "unittestPU";
    public static final String DEFAULT_JDBC_URL = "jdbc:derby:memory:unittestDB";
    public static final String DEFAULT_JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static final String URL_PROPERTY = "javax.persistence.jdbc.url";
    public static final String DRIVER_PROPERTY = "javax.persistence.jdbc.driver";
    public static final String SUFFIX = ";create=true";

    private JpaConstants() {
        // prevent instantiation
    }
}
