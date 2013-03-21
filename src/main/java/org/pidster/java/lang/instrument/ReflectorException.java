/**
 * 
 */
package org.pidster.java.lang.instrument;

/**
 * @author pidster
 *
 */
public class ReflectorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     * @param cause
     */
    public ReflectorException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

}
