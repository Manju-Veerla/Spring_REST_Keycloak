package eu.unite.recruiting.exception;

import java.io.Serial;

public class WorkshopAlreadyExistException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 53457089789182737L;
    public WorkshopAlreadyExistException(final String message) {
        super( message);
    }
}
