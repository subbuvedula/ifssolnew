package com.kickass.ifssol.messaging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.jms.Message;
import javax.jms.MessageListener;

public class SoluminaMessageListener implements MessageListener {
    private static final Logger LOGGER = LogManager.getLogger(XMLMessageConverter.class);

    public SoluminaMessageListener() {
    }

    @Override
    public void onMessage(Message message) {

    }
}
