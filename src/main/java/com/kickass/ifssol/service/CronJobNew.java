package com.kickass.ifssol.service;

import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.GenericDataMapperNew;
import com.kickass.ifssol.mapper.MappingException;
import com.kickass.ifssol.messaging.MessagePublisher;
import com.kickass.ifssol.messaging.XMLMessageConverter;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.APException;
import ifs.fnd.ap.Record;
import ifs.fnd.ap.RecordCollection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import java.util.function.Function;

public class CronJobNew implements Runnable {
    private static Logger LOGGER = LogManager.getLogger(CronJobNew.class);
    public static final String LOG_ID = "CF$_LOG_ID";
    public static final String LOG_TIMESTAMP = "cf$_log_timestamp";
    public static final String LOG_TYPE = "cf$_log_type";

    private SolNodesRoot solNodesRoot;
    private CommonDataAccessor commonDataAccessor;
    private MessagePublisher messagePublisher;
    private DocTemplateMap docTemplateMap;
    private GenericDataMapperNew genericDataMapper;

    public CronJobNew(CommonDataAccessor commonDataAccessor,
                      MessagePublisher messagePublisher,
                      SolNodesRoot solNodesRoot,
                      DocTemplateMap docTemplateMap,
                      GenericDataMapperNew genericDataMapper) {
        this.commonDataAccessor = commonDataAccessor;
        this.messagePublisher = messagePublisher;
        this.solNodesRoot = solNodesRoot;
        this.docTemplateMap = docTemplateMap;
        this.genericDataMapper = genericDataMapper;
    }

    public void run() {

        /*
        Object o = new XMLMessageConverter().toObject(XMLMessageConverter.XML);
        try {
            genericDataMapper.mapToIFS(o, solNodesRoot, docTemplateMap);
        } catch (MappingException e) {
            e.printStackTrace();
        }
        */

        RecordCollection recordCollection = null;
        try {
            LOGGER.info("Getting the records from database for " + solNodesRoot.getName());
            recordCollection = commonDataAccessor.getData(solNodesRoot.getQuery());
            LOGGER.info("Got "+ recordCollection.size() + " records from database for " + solNodesRoot.getName());

        }
        catch(APException ape) {
            LOGGER.error("Could not obtain Records from IFS Database for transaction : " + solNodesRoot.getName(), ape);
            return;
        }

        if (recordCollection == null) {
            LOGGER.error("RecordCollection is null for " + solNodesRoot.getName());
            return;
        }

        Function<Record, XmlObject> func = solNodesRoot.getMapperFunction();
        int size = recordCollection.size();
        for( int i=0; i<size; i++) {
            Record r = recordCollection.get(i);
            String logId = (String) r.findValue(LOG_ID);
            XmlObject xmlObject = null;
            try {
                if (func != null) {
                    xmlObject = func.apply(r);
                }
                else {
                    xmlObject = genericDataMapper.mapToSol(r,solNodesRoot,docTemplateMap);
                }
                messagePublisher.publish(solNodesRoot.getSendQueue(), xmlObject);
                LOGGER.info("Updating the txn status for txn : " + solNodesRoot.getName() + ", logId : " + logId);
                if (solNodesRoot.isUpdateStatus()) {
                    commonDataAccessor.runProc(solNodesRoot.getUpdateStatement(), logId);
                }
            }
            catch (Exception e) {
                LOGGER.error("Unable to update the status for " + solNodesRoot.getName(), e);
                continue;
            }

        }
    }


}
