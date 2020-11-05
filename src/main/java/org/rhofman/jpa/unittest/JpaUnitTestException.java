package org.rhofman.jpa.unittest;

public class JpaUnitTestException extends RuntimeException {

    private static final long serialVersionUID = 845934875L;

    public JpaUnitTestException(String s) {
        super(s);
    }

    public JpaUnitTestException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
