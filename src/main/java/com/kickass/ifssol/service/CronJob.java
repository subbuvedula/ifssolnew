package com.kickass.ifssol.service;

import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.TxnMapping;
import com.kickass.ifssol.mapper.GenericDataMapper;
import com.kickass.ifssol.mapper.MappingException;
import com.kickass.ifssol.messaging.MessagePublisher;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import com.kickass.ifssol.util.reflect.XStreamUtil;
import ifs.fnd.ap.APException;
import ifs.fnd.ap.Record;
import ifs.fnd.ap.RecordCollection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import java.util.function.Function;

public class CronJob implements Runnable {
    private static Logger LOGGER = LogManager.getLogger(CronJob.class);
    public static final String LOG_ID = "CF$_LOG_ID";
    public static final String LOG_TIMESTAMP = "cf$_log_timestamp";
    public static final String LOG_TYPE = "cf$_log_type";

    private TxnMapping txnMapping;
    private CommonDataAccessor commonDataAccessor;
    private MessagePublisher messagePublisher;
    private DocTemplateMap docTemplateMap;
    private GenericDataMapper genericDataMapper;

    public CronJob(CommonDataAccessor commonDataAccessor,
                   MessagePublisher messagePublisher,
                   TxnMapping txnMapping,
                   DocTemplateMap docTemplateMap,
                   GenericDataMapper genericDataMapper) {
        this.commonDataAccessor = commonDataAccessor;
        this.messagePublisher = messagePublisher;
        this.txnMapping = txnMapping;
        this.docTemplateMap = docTemplateMap;
        this.genericDataMapper = genericDataMapper;
    }

    public void run() {
        Function<Record, XmlObject> mapperFunc = txnMapping.getMapperFunction();
        String query = txnMapping.getQuery();
        String updateStatement = txnMapping.getUpdateStatement();
        RecordCollection recordCollection = null;
        try {
            recordCollection = commonDataAccessor.getData(query);
        }
        catch(APException ex) {
            LOGGER.error("Unable to get the records from IFS database", ex);
            return;
        }
        XStreamUtil xStreamUtil = new XStreamUtil();
        xStreamUtil.write(recordCollection);
        if (recordCollection == null) {
            LOGGER.warn("RecordCollection is null for " + txnMapping.getName());
            return;
        }
        int size = recordCollection.size();

        for( int i=0; i<size; i++) {
            try {
                Record r = recordCollection.get(i);
                String logId = (String) r.findValue(LOG_ID);
                XmlObject xmlObject = mapperFunc.apply(r);
                messagePublisher.publish(txnMapping.getSendQueue(), xmlObject);

                LOGGER.info("Updating the txn status for txn : " + txnMapping.getName() + ", logId : " + logId);
                if (txnMapping.isUpdateStatus()) {
                    commonDataAccessor.runProc(txnMapping.getUpdateStatement(), logId);
                }
            }
            catch (APException apException) {
                LOGGER.warn("Unable to update the status flag" , apException);
            }
        }
    }


}
