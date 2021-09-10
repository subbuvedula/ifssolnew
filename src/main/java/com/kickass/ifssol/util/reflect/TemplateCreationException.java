package com.kickass.ifssol.util.reflect;

public class TemplateCreationException extends Exception {
    public TemplateCreationException() {
        super();
    }

    public TemplateCreationException(String message) {
        super(message);
    }

    public TemplateCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateCreationException(Throwable cause) {
        super(cause);
    }

    protected TemplateCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
