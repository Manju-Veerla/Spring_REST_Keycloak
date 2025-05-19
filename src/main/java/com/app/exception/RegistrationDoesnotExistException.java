package com.app.exception;

import java.io.Serial;

public class RegistrationDoesnotExistException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 53457089789182737L;
    public RegistrationDoesnotExistException(final String message) {
        super( message);
    }
}
