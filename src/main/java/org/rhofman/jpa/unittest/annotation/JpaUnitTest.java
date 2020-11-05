package org.rhofman.jpa.unittest.annotation;

import org.rhofman.jpa.unittest.JpaConstants;

import java.lang.annotation.*;

/**
 * If jdbcUrl is definined in persistence.xml, then leaf empty
 * if jdbcDriver is defined in persistence.xml then leaf also empty
 * <pre>
 *     jdbcUrl = "jdbc:derby:memory:unittestDB"
 *     jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver"
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JpaUnitTest {
    CreateTableStrategy strategy() default CreateTableStrategy.SQL_SCRIPT;
    String jdbcUrl() default JpaConstants.DEFAULT_JDBC_URL;
    String jdbcDriver() default JpaConstants.DEFAULT_JDBC_DRIVER;
    String persistenceUnit() default JpaConstants.DEFAULT_UNIT_NAME;
    String dataSetFile() default "";
    String createTablesFile() default JpaConstants.CREATE_TABLES_SQL_FILE;
}
