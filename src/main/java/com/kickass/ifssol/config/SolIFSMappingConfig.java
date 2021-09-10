package com.kickass.ifssol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.TxnMapping;
import com.kickass.ifssol.entity.TxnMappingRoot;
import com.kickass.ifssol.mapper.GenericDataMapper;
import com.kickass.ifssol.messaging.MessagePublisher;
import com.kickass.ifssol.service.CronJob;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import com.kickass.ifssol.util.reflect.Reflector;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class SolIFSMappingConfig {
    private static Logger LOGGER = LogManager.getLogger(SolIFSMappingConfig.class);

    @Autowired
    private Environment env;

    private TxnMappingRoot txnMappingRoot;
    private Map<String, TxnMapping> txnMappingMap = new HashMap<>();
    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    @Autowired
    private GenericDataMapper genericDataMapper;

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private Reflector reflector;

    @PostConstruct
    void setup() {
        try {
            configureThreadPoolTaskScheduler();
            loadMappings();
            //schedule();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load the transaction.properties", ex);
        }
    }

    private void configureThreadPoolTaskScheduler() {
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.initialize();
    }

    void loadMappings() throws IOException {

            InputStream is = SolIFSMappingConfig.class.getResourceAsStream("/datamapping.json");
            ObjectMapper objectMapper = new ObjectMapper();
            txnMappingRoot = objectMapper.readValue(is, TxnMappingRoot.class);
            List<TxnMapping> txnMappingList = txnMappingRoot.getTxnMappings();
            if (txnMappingList != null) {
                for(TxnMapping docMap : txnMappingList) {
                    txnMappingMap.put(docMap.getName(), docMap);
                }
            }

    }

    public Map<String, TxnMapping> getTxnMappings() {
      return txnMappingMap;
    }

    private void schedule() {
        Map<String, TxnMapping> documentMappingMap = getTxnMappings();

        for(TxnMapping txnMapping : documentMappingMap.values()) {
            String cronExpression = txnMapping.getCronExpression();

            if (!txnMapping.isEnabled()) {
                continue;
            }

            DocTemplateMap docTemplateMap = null;

            try {
                docTemplateMap = reflector.process(txnMapping.getRootClass());
            } catch (ClassNotFoundException e) {
                LOGGER.error("Unable to schedule the job for " + txnMapping.getName(), e);
                throw new RuntimeException("Unable to schedule the job for " + txnMapping.getName() + ",Class Not Found for : " + txnMapping.getRootClass(), e);
            }

            if (!StringUtils.isEmpty(cronExpression)) {
                CronTrigger cronTrigger = new CronTrigger(cronExpression);
                CronJob cronJob = new CronJob(commonDataAccessor, messagePublisher, txnMapping, docTemplateMap, genericDataMapper);
                LOGGER.info("Scheduling " + txnMapping.getName() + ", cron expression " + cronExpression);
                threadPoolTaskScheduler.schedule(cronJob, cronTrigger);
            }
        }
    }

}
