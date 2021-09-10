package com.kickass.ifssol.service;

import com.kickass.ifssol.messaging.MessagePublisher;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ServiceBase {
    @Autowired
    private MessagePublisher messagePublisher;
    public void publish(String destination, XmlObject xmlObject) {
        messagePublisher.publish(destination, xmlObject);
    }
}
