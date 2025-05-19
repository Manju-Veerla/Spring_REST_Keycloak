package com.app.exception;

import java.io.Serial;

public class InvalidWorkshopDataException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 53457089789182737L;
    public InvalidWorkshopDataException(final String message) {
        super( message);
    }
}
