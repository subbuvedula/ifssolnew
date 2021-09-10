package com.kickass.ifssol.messaging;

import com.kickass.ifssol.config.SolIFSMappingConfigNew;
import com.kickass.ifssol.entity.SolNodesRoot;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;

import java.util.Collection;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {
    private static Logger LOGGER = LogManager.getLogger(JmsConfig.class);


    @Autowired
    private Environment environment;

    String BROKER_URL = "tcp://localhost:61616";
    String BROKER_USERNAME = "admin";
    String BROKER_PASSWORD = "admin";

    @Autowired
    private SolIFSMappingConfigNew solIFSMappingConfig;

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        LOGGER.info("spring.activemq.broker-url : " + environment.getProperty("spring.activemq.broker-url"));
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(environment.getProperty("spring.activemq.broker-url"));
        connectionFactory.setPassword(environment.getProperty("spring.activemq.password"));
        connectionFactory.setUserName(environment.getProperty("spring.activemq.user"));
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId("Solumina");

        Collection<SolNodesRoot> solNodesRootList = solIFSMappingConfig.getIncomingSolNodesRootList();
        boolean hasAtleatOneMesssageListener = false;
        for(SolNodesRoot solNodesRoot : solNodesRootList) {
            if (!StringUtils.isEmpty(solNodesRoot.getReceiveQueue())) {
                endpoint.setDestination(solNodesRoot.getReceiveQueue());
                endpoint.setMessageListener(new SoluminaMessageListener());
                hasAtleatOneMesssageListener = true;
            }
        }
        if (hasAtleatOneMesssageListener) {
            registrar.registerEndpoint(endpoint);
        }
    }
}