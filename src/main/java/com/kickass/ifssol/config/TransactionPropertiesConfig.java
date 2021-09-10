package com.kickass.ifssol.config;

import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.TxnMapping;
import com.kickass.ifssol.entity.TransactionMetadata;
import com.kickass.ifssol.messaging.MessagePublisher;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

//@Component
@Deprecated
//TODO DELETE
public class TransactionPropertiesConfig {
    private static Logger LOGGER = LogManager.getLogger(TransactionPropertiesConfig.class);
    private Map<String, TransactionMetadata> transactionMetadataMap = new HashMap<>();
    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    @Autowired
    private Environment env;

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    @Autowired
    private MessagePublisher messagePublisher;

    private SolIFSMappingConfig solIFSMappingConfig;

    @Autowired
    public TransactionPropertiesConfig(SolIFSMappingConfig solIFSMappingConfig) {
        this.solIFSMappingConfig = solIFSMappingConfig;
    }

    @PostConstruct
    void setup() {
        try {
            configureThreadPoolTaskScheduler();
            prepare();
            schedule();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load the transaction.properties", ex);
        }
    }

    private void configureThreadPoolTaskScheduler() {
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.initialize();
    }

    void prepare() throws IOException {
        InputStream is = TransactionPropertiesConfig.class.getResourceAsStream("/transactions.properties");
        Properties props = new Properties();
        props.load(is);
        String transactions = props.getProperty("transaction.names");
        String[] transactionsArray = transactions.split(",");
        solIFSMappingConfig.getTxnMappings();
        Map<String, TxnMapping> documentMappingMap = solIFSMappingConfig.getTxnMappings();

        for (String txn : transactionsArray) {
            txn = txn.trim();
            TransactionMetadata tmeta = new TransactionMetadata();
            tmeta.setName(txn);
            String sendq = props.getProperty(txn + ".sendq");
            String recvq = props.getProperty(txn + ".recvq");
            String updateStatement = props.getProperty(txn + ".updateStatement");

            TxnMapping txnMapping = documentMappingMap.get(txn);
            if (txnMapping == null) {
                LOGGER.warn("Document Mapping not found in datamappings.json for : , " + txn + " cannot schedule the job");
                continue;
            }

            String cronExpression = props.getProperty(txn + ".cronExpression");
            String query = props.getProperty(txn + ".query");
            String mapperFunctionName = props.getProperty(txn + ".mapperFunction");
            String updateStatus = props.getProperty(txn + ".updateStatus");

            if (mapperFunctionName != null) {
                try {
                    Class functionClass = Class.forName(mapperFunctionName);
                    Function function = (Function) functionClass.getDeclaredConstructor().newInstance();
                    tmeta.setMapperFunction(function);
                } catch (Exception ex) {
                    LOGGER.error("Invalid Mapper for " + txn, ex);
                }
            }
            tmeta.setUpdateStatement(updateStatement);
            tmeta.setCronExpression(cronExpression);
            tmeta.setSendQueue(sendq);
            tmeta.setShouldUpdateStatus(updateStatus);
            tmeta.setReceiveQueue(recvq);
            tmeta.setQuery(query);
            tmeta.setMapperFunctionName(mapperFunctionName);
            transactionMetadataMap.put(txn, tmeta);
        }
    }

    public Collection<TransactionMetadata> getTransactionMetadata() {
        return transactionMetadataMap.values();
    }

    private void schedule() {
        Map<String, TxnMapping> documentMappingMap = solIFSMappingConfig.getTxnMappings();

        Collection<TransactionMetadata>  values = getTransactionMetadata();
        for(TransactionMetadata tmeta : values) {
            String cronExpression = tmeta.getCronExpression();
            if (!StringUtils.isEmpty(cronExpression)) {
                CronTrigger cronTrigger = new CronTrigger(cronExpression);
               TxnMapping txnMapping = documentMappingMap.get(tmeta.getName());
               if (txnMapping == null) {
                   LOGGER.warn("Document Mapping not found in datamappings.json, cannot schedule the job");
                   continue;
               }
                //CronJob cronJob = new CronJob(commonDataAccessor, messagePublisher, tmeta, txnMapping);
                //LOGGER.info("Scheduling " + tmeta.getName() + ", cron expression " + cronExpression);
                //threadPoolTaskScheduler.schedule(cronJob, cronTrigger);
            }
        }
    }
}
