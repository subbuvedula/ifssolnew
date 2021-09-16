package com.kickass.ifssol.mapper;

import org.apache.xmlbeans.XmlError;
import v2.io.swagger.models.Xml;

public class MappingException extends Exception {
    private XmlError[]  xmlErrors;
    public MappingException(String message, XmlError[]  xmlErrors) {
        super(message);
        this.xmlErrors = xmlErrors;
    }
    public MappingException() {
        super();
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }

    protected MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
