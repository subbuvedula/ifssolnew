package com.kickass.ifssol.messaging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MessagePublisher {

    private static Logger logger = LogManager.getLogger(MessagePublisher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    private void setup() {
        jmsTemplate.setMessageConverter(new XMLMessageConverter());
    }

    public void publish(String destination, XmlObject xmlObject) {
        jmsTemplate.convertAndSend(destination, xmlObject);
    }



}
