package com.app.exception;

import java.io.Serial;

public class WorkshopNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 53457089789182737L;

    public WorkshopNotFoundException(final String message) {
        super(message);
    }
}
