package com.kickass.ifssol.ifsproxy;

public class IFSServerException extends Exception {

    public IFSServerException() {
    }

    public IFSServerException(String message) {
        super(message);
    }

    public IFSServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IFSServerException(Throwable cause) {
        super(cause);
    }

    public IFSServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
