package org.pidster.java.lang.instrument;

public class TargetClassInstantiationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TargetClassInstantiationException(String message, Throwable thrown) {
        super(message, thrown);
    }

    public TargetClassInstantiationException(Throwable thrown) {
        super(thrown);
    }

}
