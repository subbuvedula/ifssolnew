package com.kickass.ifssol.messaging;

import com.kickass.ifssol.config.SolIFSMappingConfigNew;
import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.MappingException;
import com.kickass.ifssol.mapper.SolToIfsMapper;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.Reflector;
import ifs.fnd.ap.APException;
import ifs.fnd.ap.PlsqlCommand;
import ifs.fnd.ap.Record;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class SoluminaMessageListener implements MessageListener {
    private static final Logger LOGGER = LogManager.getLogger(XMLMessageConverter.class);

    @Autowired
    private SolIFSMappingConfigNew config;

    @Autowired
    private SolToIfsMapper solToIfsMapper;

    @Autowired
    private Reflector reflector;

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    public SoluminaMessageListener() {
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)message;
            try {
                ActiveMQQueue destination  = (ActiveMQQueue)textMessage.getJMSDestination();
                String queueName = destination.getQueueName();
                SolNodesRoot root = config.getByQueueName(queueName);
                root.getUpdateStatement();
                String xmlString = textMessage.getText();
                LOGGER.info(xmlString);
                XmlObject xmlObject = XmlObject.Factory.parse(xmlString);
                DocTemplate docTemplate = reflector.process(xmlObject.getClass(), true);

                PlsqlCommand command = commonDataAccessor.getPlSqlCommand(root.getUpdateStatement());
                Record record = command.getBindVariables();
                solToIfsMapper.map(xmlObject, root, docTemplate.getDocTemplateMap(), record);
                commonDataAccessor.execute(command);
            } catch (JMSException | XmlException | MappingException | APException e) {
                LOGGER.error("Failed to get message from the queue", e);
            }
        }
    }
}
