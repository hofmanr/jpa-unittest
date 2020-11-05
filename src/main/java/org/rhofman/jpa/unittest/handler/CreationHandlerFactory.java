package org.rhofman.jpa.unittest.handler;

import org.rhofman.jpa.unittest.JpaUnitTestException;
import org.rhofman.jpa.unittest.annotation.CreateTableStrategy;

public class CreationHandlerFactory {

    private CreationHandlerFactory() {
    }

    public static CreationHandler getHandler(CreateTableStrategy creationStrategy) {
        switch (creationStrategy) {
            case ENTITY_ANNOTATIONS:
                return new EntityCreationHandler();
            case SQL_SCRIPT:
                return new SqlCreationHandler();
            default:
                throw new JpaUnitTestException("Unkown creation strategy " + creationStrategy);
        }
    }
}
